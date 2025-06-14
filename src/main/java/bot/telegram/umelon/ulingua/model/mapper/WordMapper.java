package bot.telegram.umelon.ulingua.model.mapper;

import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WordMapper {
    WordMapper MAPPER = Mappers.getMapper(WordMapper.class);

    WordDto toDto(Word entity);
    Word toEntity(WordDto dto);
}
