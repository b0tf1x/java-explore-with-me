package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.model.Location;
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}
