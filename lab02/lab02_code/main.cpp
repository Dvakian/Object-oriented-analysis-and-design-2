#include "raylib.h"

using namespace std;

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

//BASE CHARACTER

class BaseCharacter : public Character
{
private:
	float speed = 250, hp = 100, maxHP = 100, damage = 10;

public:
	float getSpeed() override { return speed; }
	float getDamage() override { return damage; }
	float getHP() override { return hp; }

	void update(float dt) override {}

	void move(Vector2& pos, float dt) override
	{
		if (IsKeyDown(KEY_W))
			pos.y -= speed * dt;
		if (IsKeyDown(KEY_S))
			pos.y += speed * dt;
		if (IsKeyDown(KEY_A))
			pos.x -= speed * dt;
		if (IsKeyDown(KEY_D))
			pos.x += speed * dt;
	}

	void increaseMaxHP(float amount) override
	{
		maxHP += amount;
		hp += amount;
		if (hp > maxHP)
			hp = maxHP;
	}

	void takeDamage(float amount) override
	{
		hp -= amount;
		if (hp > maxHP)
			hp = maxHP;
	}
};

class CharacterDecorator : public Character
{
protected:
	Character* character;
public:
	CharacterDecorator(Character* c) : character(c) {}

	virtual ~CharacterDecorator() { delete character; }

	float getSpeed() override { return character->getSpeed(); }
	float getDamage() override { return character->getDamage(); }
	float getHP() override { return character->getHP(); }

	void increaseMaxHP(float amount) override { character->increaseMaxHP(amount); }

	void update(float dt) override { character->update(dt); }

	void move(Vector2& pos, float dt) override
	{
		float speed = getSpeed();

		if (IsKeyDown(KEY_W))
			pos.y -= speed * dt;
		if (IsKeyDown(KEY_S))
			pos.y += speed * dt;
		if (IsKeyDown(KEY_A))
			pos.x -= speed * dt;
		if (IsKeyDown(KEY_D))
			pos.x += speed * dt;
	}

	void takeDamage(float amount) override { character->takeDamage(amount); }


	Color color() override { return character->color(); }
};


//DEBUFFS
class Frozen : public CharacterDecorator
{
	float timer;

public:
	Frozen(Character* c, float t) : CharacterDecorator(c), timer(t) {}

	void update(float dt) override
	{
		character->update(dt);
		timer -= dt;
	}

	void move(Vector2& pos, float dt) override
	{
		if (timer > 0)
			return;
		character->move(pos, dt);
	}

	Color color() override
	{
		if (timer > 0)
			return SKYBLUE;
		return character->color();
	}
};

class Burning : public CharacterDecorator
{
	float timer, damagePerSecond;

public:
	Burning(Character* c, float t, float dps) : CharacterDecorator(c), timer(t), damagePerSecond(dps) {}

	void update(float dt) override
	{
		character->update(dt);

		if (timer > 0)
		{
			character->takeDamage(damagePerSecond * dt / 2);
			timer -= dt;
		}
	}

	Color color() override
	{
		if (timer > 0)
			return ORANGE;
		return character->color();
	}
};

class Electrified : public CharacterDecorator
{
	float timer;

public:
	Electrified(Character* c, float t) : CharacterDecorator(c), timer(t) {}

	void update(float dt) override
	{
		character->update(dt);
		timer -= dt;
	}

	float getSpeed() override
	{
		if (timer > 0)
			return character->getSpeed() * 0.5f;
		return character->getSpeed();
	}

	Color color() override
	{
		if (timer > 0)
			return VIOLET;
		return character->color();
	}
};

//CLOTHES
class Boots : public CharacterDecorator
{
public:
	Boots(Character* c) : CharacterDecorator(c) {}

	float getSpeed() override
	{
		return character->getSpeed() * 1.05f;
	}
};

class Pants : public CharacterDecorator
{
public:
	Pants(Character* c) : CharacterDecorator(c) {}

	float getDamage() override
	{
		return character->getDamage() * 1.2f;
	}
};

class Shirt : public CharacterDecorator
{
public:
	Shirt(Character* c) : CharacterDecorator(c)
	{
		character->increaseMaxHP(50);
	}

	float getHP() override
	{
		return character->getHP();
	}
};

class Ring : public CharacterDecorator
{
public:
	Ring(Character* c) : CharacterDecorator(c)
	{
		character->increaseMaxHP(5);
	}

	float getSpeed() override
	{
		return character->getSpeed() * 1.005f;
	}

	float getDamage() override
	{
		return character->getDamage() * 1.005f;
	}

	float getHP() override
	{
		return character->getHP() * 1.005f;
	}
};

//BUFF

class Regeneration : public CharacterDecorator
{
	float timer, regenPerSecond;
public:
	Regeneration(Character* c, float t) : CharacterDecorator(c), timer(t), regenPerSecond(5.0f) {}
	void update(float dt) override
	{
		character->update(dt);
		if (timer > 0)
		{
			character->takeDamage(-regenPerSecond * dt / 2);
			timer -= dt;
		}
	}
	Color color() override
	{
		if (timer > 0)
			return LIME;
		return character->color();
	}

};

int main()
{
	InitWindow(900, 600, "Decorator Character System");
	SetTargetFPS(60);

	Vector2 playerPos = { 450, 300 };
	Character* player = new BaseCharacter();

	bool isDead = false;

	while (!WindowShouldClose())
	{
		float dt = GetFrameTime();

		if (player->getHP() <= 0.0f)
			isDead = true;

		if (!isDead)
		{
			player->update(dt);
			player->move(playerPos, dt);

			if (IsKeyPressed(KEY_ONE))
				player = new Frozen(player, 3.0f);

			if (IsKeyPressed(KEY_TWO))
				player = new Burning(player, 5.0f, 10.0f);

			if (IsKeyPressed(KEY_THREE))
				player = new Electrified(player, 5.0f);

			if (IsKeyPressed(KEY_Z))
				player = new Boots(player);

			if (IsKeyPressed(KEY_X))
				player = new Pants(player);

			if (IsKeyPressed(KEY_C))
				player = new Shirt(player);

			if (IsKeyPressed(KEY_V))
				player = new Ring(player);

			if (IsKeyPressed(KEY_H))
				player = new Regeneration(player, 5.0f);

			if (IsKeyPressed(KEY_R))
				isDead = true;
		}

		if (isDead && IsKeyPressed(KEY_R))
		{
			delete player;
			player = new BaseCharacter();
			playerPos = { 450, 300 };
			isDead = false;
		}

		BeginDrawing();
		ClearBackground(BLACK);
		if (!isDead)
			DrawCircleV(playerPos, 20, player->color());
		else
			DrawText("YOU DIED! Press R to restart", 250, 280, 30, RED);

		DrawText("1 Frozen | 2 Burning | 3 Electric", 10, 10, 20, WHITE);
		DrawText("Z Boots | X Pants | C Shirt | V Ring | H Heal | R restart", 10, 40, 20, WHITE);

		DrawText(TextFormat("Speed: %.0f", player->getSpeed()), 10, 100, 20, GREEN);
		DrawText(TextFormat("Damage: %.0f", player->getDamage()), 10, 130, 20, RED);
		DrawText(TextFormat("HP: %.0f", player->getHP()), 10, 160, 20, YELLOW);

		EndDrawing();
	}

	delete player;
	CloseWindow();
}