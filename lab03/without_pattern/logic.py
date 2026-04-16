class Step:
    def __init__(self, dx, dy):
        self.dx = dx
        self.dy = dy

    def execute(self, player):
        player.move(self.dx, self.dy)


class Player:
    def __init__(self):
        self.cords = [1, 1]

    def move(self, dx, dy):
        if 1 <= self.cords[0] + dx <= 2:
            self.cords[0] += dx
        if 1 <= self.cords[1] + dy <= 4:
            self.cords[1] += dy
