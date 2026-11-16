"""Reproducción de la pista MP3 del menú mediante Windows Multimedia."""
import ctypes
import sys


class MusicaMenu:
    ALIAS = "pandemic_menu"
    INICIO_MS = 5000

    def __init__(self, root, archivo):
        self.root = root
        self.archivo = archivo
        self.abierta = False
        self.revision = None
        self.mci = None
        if sys.platform == "win32":
            self.mci = ctypes.windll.winmm.mciSendStringW
            self.mci.argtypes = (ctypes.c_wchar_p, ctypes.c_wchar_p, ctypes.c_uint, ctypes.c_void_p)
            self.mci.restype = ctypes.c_uint

    def comando(self, instruccion):
        respuesta = ctypes.create_unicode_buffer(128)
        error = self.mci(instruccion, respuesta, len(respuesta), None)
        if error:
            raise OSError(f"Windows Multimedia ({error}): {instruccion}")
        return respuesta.value

    def iniciar(self):
        if self.mci is None or not self.archivo.is_file():
            return
        self.detener()
        try:
            self.comando(f'open "{self.archivo}" type mpegvideo alias {self.ALIAS}')
            self.abierta = True
            self.comando(f"set {self.ALIAS} time format milliseconds")
            self.reproducir()
            self.revision = self.root.after(1000, self.repetir)
        except OSError:
            self.detener()

    def reproducir(self):
        self.comando(f"play {self.ALIAS} from {self.INICIO_MS}")

    def repetir(self):
        self.revision = None
        try:
            if self.comando(f"status {self.ALIAS} mode") == "stopped":
                self.reproducir()
            self.revision = self.root.after(1000, self.repetir)
        except OSError:
            self.detener()

    def detener(self):
        if self.revision is not None:
            self.root.after_cancel(self.revision)
            self.revision = None
        if self.abierta:
            self.abierta = False
            try:
                self.comando(f"close {self.ALIAS}")
            except OSError:
                pass
