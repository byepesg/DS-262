class ColaCircular:
    def __init__(self, capacidad: int):
        if capacidad <= 0:
            raise ValueError("La capacidad de la cola debe ser mayor a 0.")
        self.capacidad: int = capacidad
        self.buffer: list[float] = [0.0] * capacidad
        self.head: int = 0
        self.tail: int = 0
        self.size: int = 0

    def esta_vacia(self) -> bool:
        return self.size == 0

    def esta_llena(self) -> bool:
        return self.size == self.capacidad

    def tamano_actual(self) -> int:
        return self.size

    def encolar(self, dato: float) -> None:
        if self.esta_llena():
            raise OverflowError("La cola circular está llena. Desencole antes de insertar.")
        self.buffer[self.tail] = float(dato)
        self.tail = (self.tail + 1) % self.capacidad
        self.size += 1

    def desencolar(self) -> float:
        if self.esta_vacia():
            raise IndexError("La cola circular está vacía.")
        dato = self.buffer[self.head]
        self.head = (self.head + 1) % self.capacidad
        self.size -= 1
        return dato

    def obtener_ventana(self) -> list[float]:
        """
        Devuelve una copia ordenada cronológicamente de los elementos actuales
        sin vaciar la cola (necesario para la ventana deslizante).
        """
        ventana: list[float] = [0.0] * self.size
        for i in range(self.size):
            indice_real = (self.head + i) % self.capacidad
            ventana[i] = self.buffer[indice_real]
        return ventana