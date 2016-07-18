package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    HealthIndicator gotoHealthIndecator() {
        return () -> Health.status("I <3 Chicago!").build();
    }

    @Bean
    CommandLineRunner runner(ReservationRepository repository) {
        return args -> {
            Arrays.asList("Josh,Julie,Michael,Peter".split(","))
                    .forEach(n -> repository.save(new Reservation(n)));

            repository.findByReservationName("Julie").forEach(System.out::println);

            repository.findAll().forEach(System.out::println);
        };

    }
}

@Controller
class ReservationMvcController {
    @Autowired
    ReservationRepository reservationRepository;

    @RequestMapping("/reservations.php")
    String page(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        return "reservations";
    }
}

@RestController
class ReservationRestController {
    @Autowired
    private ReservationRepository reservationRepository;

    @RequestMapping("/reservations")
    Collection<Reservation> reservations() {
        return reservationRepository.findAll();
    }

}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //select * from reservations where reservation_name = :rn
    Collection<Reservation> findByReservationName(@Param("rn") String rn);
}

@Entity
class Reservation {
    @Id
    @GeneratedValue
    private Long id;
    private String reservationName;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }

    Reservation() {
    }

    public Reservation(String reservationName) {
        this.reservationName = reservationName;
    }

    public Long getId() {
        return id;
    }

    public String getReservationName() {
        return reservationName;
    }
}