#include "raylib.h"
#include <vector>
#include <memory>
#include <cmath>

using namespace std;

//Базовый класс эффекта

class Effect {
public:
    float timer;
    virtual ~Effect() {}

    virtual void apply(float dt, float& speed, float& damage, float& hp, float& maxHP) = 0;
    virtual Color getColor() const { return BLUE; }
    virtual bool isActive() const { return timer > 0; }
};

//Конкретные эффекты

class FrozenEffect : public Effect {
public:
    FrozenEffect(float t) { timer = t; }

    void apply(float dt, float& speed, float& damage, float& hp, float& maxHP) override {
        timer -= dt;
    }

    Color getColor() const override {
        return timer > 0 ? SKYBLUE : BLUE;
    }
};

class BurningEffect : public Effect {
public:
    float damagePerSecond;

    BurningEffect(float t, float dps) : damagePerSecond(dps) {
        timer = t;
    }

    void apply(float dt, float& speed, float& damage, float& hp, float& maxHP) override {
        if (timer > 0) {
            hp -= damagePerSecond * dt;
            if (hp < 0) hp = 0;
            timer -= dt;
        }
    }

    Color getColor() const override {
        return timer > 0 ? ORANGE : BLUE;
    }
};

class ElectrifiedEffect : public Effect {
public:
    ElectrifiedEffect(float t) { timer = t; }

    void apply(float dt, float& speed, float& damage, float& hp, float& maxHP) override {
        if (timer > 0) {
            speed *= 0.5f;
        }
        timer -= dt;
    }

    Color getColor() const override {
        return timer > 0 ? VIOLET : BLUE;
    }
};

class RegenerationEffect : public Effect {
public:
    float regenPerSecond;

    RegenerationEffect(float t) {
        regenPerSecond = 5.0f;
        timer = t;
    }

    void apply(float dt, float& speed, float& damage, float& hp, float& maxHP) override {
        if (timer > 0) {
            hp += regenPerSecond * dt;
            if (hp > maxHP) hp = maxHP;
        }
        timer -= dt;
    }

    Color getColor() const override {
        return timer > 0 ? LIME : BLUE;
    }
};

//класс персонажа
class Character
{
public:
    virtual float getSpeed() = 0;
    virtual float getDamage() = 0;
    virtual float getHP() = 0;
    virtual Color color() { return BLUE; }
    virtual void increaseMaxHP(float amount) = 0;
    virtual void update(float dt) = 0;
    virtual void move(Vector2& pos, float dt) = 0;
    virtual void takeDamage(float amount) = 0;
    virtual ~Character() {}
};

class BaseCharacter : public Character
{
private:
    float baseSpeed = 250;
    float baseHP = 100;
    float maxHP = 100;
    float baseDamage = 10;

	vector<unique_ptr<Effect>> activeEffects; //храним эффекты в виде указателей

    int bootsCount = 0;
    int pantsCount = 0;
    int shirtCount = 0;
    int ringCount = 0;

    bool isFrozen() const {
        for (const auto& effect : activeEffects) {
            if (dynamic_cast<const FrozenEffect*>(effect.get()) && effect->isActive())
                return true;
        }
        return false;
    }

public:

    float getSpeed() override {
        float result = baseSpeed;

        result *= pow(1.05f, bootsCount);
        result *= pow(1.005f, ringCount);

        for (const auto& effect : activeEffects) {
            if (dynamic_cast<const ElectrifiedEffect*>(effect.get()) && effect->isActive())
                result *= 0.5f;
        }

        return result;
    }

    float getDamage() override {
        float result = baseDamage;

        result *= pow(1.2f, pantsCount);
        result *= pow(1.005f, ringCount);

        return result;
    }

    float getHP() override {
        return baseHP;
    }

    Color color() override {
        for (const auto& effect : activeEffects) {
            if (effect->isActive())
                return effect->getColor();
        }
        return BLUE;
    }

    void increaseMaxHP(float amount) override {
        maxHP += amount;
        baseHP += amount;

        if (baseHP > maxHP)
            baseHP = maxHP;
    }

    void takeDamage(float amount) override {
        baseHP -= amount;

        if (baseHP < 0)
            baseHP = 0;
    }

    void update(float dt) override {
        for (auto it = activeEffects.begin(); it != activeEffects.end(); )
        {
            float speed = baseSpeed;
            float damage = baseDamage;

            (*it)->apply(dt, speed, damage, baseHP, maxHP);

            if (!(*it)->isActive())
                it = activeEffects.erase(it);
            else
                ++it;
        }
    }

    void move(Vector2& pos, float dt) override {
        if (isFrozen()) return;

        float speed = getSpeed();

        if (IsKeyDown(KEY_W)) pos.y -= speed * dt;
        if (IsKeyDown(KEY_S)) pos.y += speed * dt;
        if (IsKeyDown(KEY_A)) pos.x -= speed * dt;
        if (IsKeyDown(KEY_D)) pos.x += speed * dt;
    }

    //эффекты
    void applyFrozen(float time) {
        activeEffects.emplace_back(make_unique<FrozenEffect>(time));
    }

    void applyBurning(float time, float dps) {
        activeEffects.emplace_back(make_unique<BurningEffect>(time, dps));
    }

    void applyElectrified(float time) {
        activeEffects.emplace_back(make_unique<ElectrifiedEffect>(time));
    }

    void applyRegeneration(float time) {
        activeEffects.emplace_back(make_unique<RegenerationEffect>(time));
    }

    //одежда
    void addBoots() {
        bootsCount++;
    }

    void addPants() {
        pantsCount++;
    }

    void addShirt() {
        shirtCount++;
        increaseMaxHP(50);
    }

    void addRing() {
        ringCount++;
        increaseMaxHP(5);
    }
};

int main()
{
    InitWindow(900, 600, "Character without pattern");
    SetTargetFPS(60);

    Vector2 playerPos = { 450, 300 };
    BaseCharacter player;

    bool isDead = false;

    while (!WindowShouldClose())
    {
        float dt = GetFrameTime();

        if (player.getHP() <= 0.0f)
            isDead = true;

        if (!isDead)
        {
            player.update(dt);
            player.move(playerPos, dt);

            // Дебаффы
            if (IsKeyPressed(KEY_ONE))
                player.applyFrozen(3.0f);

            if (IsKeyPressed(KEY_TWO))
                player.applyBurning(5.0f, 10.0f);

            if (IsKeyPressed(KEY_THREE))
                player.applyElectrified(5.0f);

            // Одежда
            if (IsKeyPressed(KEY_Z))
                player.addBoots();

            if (IsKeyPressed(KEY_X))
                player.addPants();

            if (IsKeyPressed(KEY_C))
                player.addShirt();

            if (IsKeyPressed(KEY_V))
                player.addRing();

            if (IsKeyPressed(KEY_H))
                player.applyRegeneration(5.0f);
        }

        if (isDead && IsKeyPressed(KEY_R))
        {
            player = BaseCharacter();
            playerPos = { 450, 300 };
            isDead = false;
        }

        BeginDrawing();
        ClearBackground(BLACK);

        if (!isDead)
            DrawCircleV(playerPos, 20, player.color());
        else
            DrawText("YOU DIED! Press R to restart", 250, 280, 30, RED);

        DrawText("1 Frozen | 2 Burning | 3 Electric", 10, 10, 20, WHITE);
        DrawText("Z Boots | X Pants | C Shirt | V Ring | H Heal | R restart", 10, 40, 20, WHITE);

        DrawText(TextFormat("Speed: %.0f", player.getSpeed()), 10, 100, 20, GREEN);
        DrawText(TextFormat("Damage: %.0f", player.getDamage()), 10, 130, 20, RED);
        DrawText(TextFormat("HP: %.0f", player.getHP()), 10, 160, 20, YELLOW);

        EndDrawing();
    }

    CloseWindow();
}