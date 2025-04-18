class Config:
    _instance = None
    _initialized = False

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self, d=None):
        if not Config._initialized:
            self.settings = d if d is not None else {}
            Config._initialized = True

    def get(self, param, default=None):
        return self.settings.get(param, default)

    def set(self, param, value):
        self.settings[param] = value