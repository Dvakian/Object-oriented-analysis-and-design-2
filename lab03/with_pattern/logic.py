from __future__ import annotations

from abc import ABC, abstractmethod


class Step:
    def __init__(self, command: Command):
        self.command = command

    def execute(self):
        self.command.execute()


class Player:
    def __init__(self):
        self.cords = [1, 1]

    def move(self, dx, dy):
        if 1 <= self.cords[0] + dx <= 2:
            self.cords[0] += dx
        if 1 <= self.cords[1] + dy <= 4:
            self.cords[1] += dy


class Command(ABC):
    def __init__(self, player: Player):
        self.player = player

    @abstractmethod
    def execute(self):
        pass


class RightStep(Command):
    def execute(self):
        self.player.move(1, 0)


class LeftStep(Command):
    def execute(self):
        self.player.move(-1, 0)


class DownStep(Command):
    def execute(self):
        self.player.move(0, 1)


class TopStep(Command):
    def execute(self):
        self.player.move(0, -1)
