package bot.telegram.umelon.ulingua.model.mapper;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.entity.Language;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LanguageMapper {
    LanguageMapper MAPPER = Mappers.getMapper(LanguageMapper.class);

    LanguageDto toDto(Language entity);
    List<LanguageDto> toDtoList(List<Language> entities);
    Language toEntity(LanguageDto dto);
}
