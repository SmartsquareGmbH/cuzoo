package de.smartsquare.cuzoo.customer.points;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CPointRepository extends JpaRepository<CPoint, Long> {
}
