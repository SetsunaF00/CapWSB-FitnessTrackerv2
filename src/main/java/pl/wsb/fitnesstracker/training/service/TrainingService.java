package pl.wsb.fitnesstracker.training.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.dto.TrainingCreateDto;
import pl.wsb.fitnesstracker.training.dto.TrainingUpdateDto;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.training.repository.TrainingRepository;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.repository.UserRepository;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    public List<Training> getAllTrainingsByUser(Long userId) {
        return trainingRepository.findByUserId(userId);
    }

    public List<Training> getAllFinishedAfter(Date afterTime) {
        return trainingRepository.findAll().stream()
                .filter(t -> t.getEndTime().after(afterTime))
                .collect(Collectors.toList());
    }

    public List<Training> getAllByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    public Training createTraining(TrainingCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Training training = new Training(
                user,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getActivityType(),
                dto.getDistance(),
                dto.getAverageSpeed()
        );

        return trainingRepository.save(training);
    }

    public Training updateTraining(Long trainingId, TrainingUpdateDto dto) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new NoSuchElementException("Training not found"));

        if (dto.getStartTime() != null) training.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) training.setEndTime(dto.getEndTime());
        if (dto.getActivityType() != null) training.setActivityType(dto.getActivityType());
        training.setDistance(dto.getDistance());
        training.setAverageSpeed(dto.getAverageSpeed());

        return trainingRepository.save(training);
    }
}
