import pygame as pg

import logic

pg.init()


class Game:
    def __init__(self):
        self.sc = pg.display.set_mode((800, 470))
        self.player = logic.Player()
        self.end = False
        self.dead = False

        self.steps: list[logic.Step] = []

        self.is_running = False
        self.last_step_time = 0

    def reset(self):
        self.player = logic.Player()
        self.end = False
        self.dead = False
        self.steps.clear()
        self.is_running = False
        self.last_step_time = 0

    def get_steps_text(self):
        names = []

        for step in self.steps:
            command = step.command

            if isinstance(command, logic.LeftStep):
                names.append("L")
            elif isinstance(command, logic.RightStep):
                names.append("R")
            elif isinstance(command, logic.TopStep):
                names.append("U")
            elif isinstance(command, logic.DownStep):
                names.append("D")

        return " ".join(names)

    def mainloop(self):
        while True:
            if self.player.cords == [2, 4]:
                self.end = True
                self.is_running = False

            if self.player.cords == [1, 2]:
                self.dead = True
                self.is_running = False

            current_time = pg.time.get_ticks()

            if self.is_running and not self.end:
                if len(self.steps):
                    if current_time - self.last_step_time >= 500:
                        self.steps.pop(0).execute()
                        self.last_step_time = current_time
                else:
                    self.is_running = False

            self.draw()

            for event in pg.event.get():
                if event.type == pg.KEYDOWN and not self.end and not self.is_running:
                    if event.key == pg.K_LEFT:
                        self.steps.append(logic.Step(logic.LeftStep(self.player)))

                    elif event.key == pg.K_RIGHT:
                        self.steps.append(logic.Step(logic.RightStep(self.player)))

                    elif event.key == pg.K_DOWN:
                        self.steps.append(logic.Step(logic.DownStep(self.player)))

                    elif event.key == pg.K_UP:
                        self.steps.append(logic.Step(logic.TopStep(self.player)))

                    elif event.key == pg.K_r:
                        self.reset()

                    elif event.key == pg.K_SPACE:
                        self.is_running = True
                        self.last_step_time = current_time

                if event.type == pg.QUIT:
                    exit()

    def draw(self):
        self.sc.fill((0, 0, 0))

        if self.end:
            f1 = pg.font.Font(None, 36)
            text1 = f1.render("Конец", True, pg.Color("pink"))
            self.sc.blit(text1, (364, 264))

        if self.dead:
            f1 = pg.font.Font(None, 36)
            text1 = f1.render("Сюда нельзя - смерть", True, pg.Color("red"))
            self.sc.blit(text1, (364, 264))

        for i in range(8):
            pg.draw.rect(
                self.sc,
                pg.Color("white") if i != 7 else pg.Color("green"),
                (
                    20 + 100 * (i % 2) + 5 * (i % 2),
                    20 + 100 * (i // 2) + 5 * (i // 2),
                    100,
                    100,
                ),
            )
            if i == 2:
                pg.draw.rect(
                    self.sc,
                    pg.Color("red"),
                    (
                        20 + 100 * (i % 2) + 5 * (i % 2),
                        20 + 100 * (i // 2) + 5 * (i // 2),
                        100,
                        100,
                    ),
                )

        pg.draw.circle(
            self.sc,
            pg.Color("darkviolet"),
            (
                20 + 50 + 105 * (self.player.cords[0] - 1),
                20 + 50 + 105 * (self.player.cords[1] - 1),
            ),
            25,
        )

        info_font = pg.font.Font(None, 32)
        text = info_font.render(
            "Управление: стрелки, старт: пробел, рестарт: R", True, pg.Color("yellow")
        )

        command_font = pg.font.Font(None, 28)
        steps_text = command_font.render(
            "Команды: " + self.get_steps_text(), True, pg.Color("darkgray")
        )
        self.sc.blit(text, (260, 30))
        self.sc.blit(steps_text, (260, 70))

        pg.display.update()
