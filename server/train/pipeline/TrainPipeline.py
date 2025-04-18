

class TrainStage:
    def do_task(self, data):
        return None

class TrainPipeline:
    def __init__(self):
        self.stages = []

    def add_stage(self, stage: TrainStage):
        self.stages.append(stage)
    
    def start_pipeline(self, data):
        for stage in self.stages:
            data = stage.do_task(data)
        return data
