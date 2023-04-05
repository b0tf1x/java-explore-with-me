package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.mapper.EventMapper.FORMATTER;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final CategoriesRepository categoriesRepository;
    @Override
    public EventFullDto create(Long userId, CreateEventDto createEventDto){
        if (createEventDto.getEventDate().isBefore(LocalDateTime.now())){
            throw new BadRequestException("Дата уже прошла");
        }
        User user = userRepository.findById(userId).orElseThrow(()->{
            throw new NotFoundException("Пользователь не найден");
        });
        Category category = categoriesRepository.findById(createEventDto.getCategory()).orElseThrow(()->{
            throw new NotFoundException("Категория не найдена");
        });
        Event event = eventsRepository.save(EventMapper.toEvent(createEventDto,user,category));
        EventFullDto eventFullDto = EventMapper.eventFullDto(event);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }
    @Override
    public List<EventShortDto> getByCreator(Long userId, Pageable pageable){
        List<EventShortDto>eventShortDtoList = eventsRepository.findByInitiator(userId)
                .stream()
                .map(EventMapper::toShortFromModel)
                .collect(Collectors.toList());
        return eventShortDtoList;
    }
}
