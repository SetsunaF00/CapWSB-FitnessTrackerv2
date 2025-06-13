package pl.wsb.fitnesstracker.training.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.dto.TrainingDto;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.training.repository.TrainingRepository;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public List<TrainingDto> getAllTrainings() {
        return trainingRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<TrainingDto> getAllTrainingsByUser(Long userId) {
        return trainingRepository.findByUserId(userId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<TrainingDto> getAllFinishedAfter(java.sql.Date afterTime) {
        return trainingRepository.findAll().stream()
                .filter(t -> t.getEndTime().after(afterTime))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TrainingDto> getAllByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TrainingDto createTraining(TrainingDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Training training = new Training(user, dto.startTime(), dto.endTime(), dto.activityType(), dto.distance(), dto.averageSpeed());
        Training saved = trainingRepository.save(training);
        return mapToDto(saved);
    }

    public TrainingDto updateTraining(Long id, TrainingDto dto) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training not found"));

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        training.setUser(user);
        training.setStartTime(dto.startTime());
        training.setEndTime(dto.endTime());
        training.setActivityType(dto.activityType());
        training.setDistance(dto.distance());
        training.setAverageSpeed(dto.averageSpeed());

        Training updated = trainingRepository.save(training);
        return mapToDto(updated);
    }

    private TrainingDto mapToDto(Training training) {
        return new TrainingDto(
                training.getId(),
                training.getUser().getId(),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }
}
