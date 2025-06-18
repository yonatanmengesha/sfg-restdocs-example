package guru.springframework.sfg_restdocs_example.web.mappers;

import guru.springframework.sfg_restdocs_example.domain.Beer;
import guru.springframework.sfg_restdocs_example.web.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper(uses={DateMapper.class})
public interface BeerMapper {

    BeerDto BeerToBeerDto(Beer beer);

    Beer BeerDtoToBeer(BeerDto dto);
}
