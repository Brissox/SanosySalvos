package petly.sanosysalvos.cl.geolocalizacion.Repository;

import com.geo.model.Location;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = """
        SELECT * FROM locations
        WHERE ST_DWithin(
            geom,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326),
            :radius
        )
    """, nativeQuery = true)
    List<Location> findNearby(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radius") double radius
    );
}