package pl.wsb.fitnesstracker.training.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.dto.TrainingDto;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.training.service.TrainingService;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping
    public ResponseEntity<List<TrainingDto>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TrainingDto>> getTrainingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(trainingService.getAllTrainingsByUser(userId));
    }

    @GetMapping("/finished/{afterTime}")
    public ResponseEntity<List<TrainingDto>> getAllFinishedAfter(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate afterTime) {
        return ResponseEntity.ok(trainingService.getAllFinishedAfter(Date.valueOf(afterTime)));
    }

    @GetMapping("/activityType")
    public ResponseEntity<List<TrainingDto>> getByActivityType(@RequestParam ActivityType activityType) {
        return ResponseEntity.ok(trainingService.getAllByActivityType(activityType));
    }

    @PostMapping
    public ResponseEntity<TrainingDto> createTraining(@RequestBody TrainingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingService.createTraining(dto));
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<TrainingDto> updateTraining(@PathVariable Long trainingId, @RequestBody TrainingDto dto) {
        return ResponseEntity.ok(trainingService.updateTraining(trainingId, dto));
    }
}
