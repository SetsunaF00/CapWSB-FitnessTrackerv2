package pl.wsb.fitnesstracker.training.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.dto.TrainingCreateDto;
import pl.wsb.fitnesstracker.training.dto.TrainingUpdateDto;
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
    public ResponseEntity<List<?>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<?>> getTrainingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(trainingService.getAllTrainingsByUser(userId));
    }

    @GetMapping("/finished/{afterTime}")
    public ResponseEntity<List<?>> getAllFinishedAfter(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate afterTime) {
        return ResponseEntity.ok(trainingService.getAllFinishedAfter(Date.valueOf(afterTime)));
    }

    @GetMapping("/activityType")
    public ResponseEntity<List<?>> getByActivityType(@RequestParam ActivityType activityType) {
        return ResponseEntity.ok(trainingService.getAllByActivityType(activityType));
    }

    @PostMapping
    public ResponseEntity<?> createTraining(@RequestBody TrainingCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingService.createTraining(dto));
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<?> updateTraining(@PathVariable Long trainingId, @RequestBody TrainingUpdateDto dto) {
        return ResponseEntity.ok(trainingService.updateTraining(trainingId, dto));
    }
}
