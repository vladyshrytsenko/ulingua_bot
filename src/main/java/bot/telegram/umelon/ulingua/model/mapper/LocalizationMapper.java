package bot.telegram.umelon.ulingua.model.mapper;

import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.entity.Localization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocalizationMapper {
    LocalizationMapper MAPPER = Mappers.getMapper(LocalizationMapper.class);

    LocalizationDto toDto(Localization entity);
}
