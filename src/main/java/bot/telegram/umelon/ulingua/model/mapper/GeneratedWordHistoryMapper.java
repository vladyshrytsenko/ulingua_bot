package bot.telegram.umelon.ulingua.model.mapper;

import bot.telegram.umelon.ulingua.model.dto.GeneratedWordHistoryDto;
import bot.telegram.umelon.ulingua.model.entity.redis.GeneratedWordHistory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GeneratedWordHistoryMapper {
    GeneratedWordHistoryMapper MAPPER = Mappers.getMapper(GeneratedWordHistoryMapper.class);

    GeneratedWordHistoryDto toDto(GeneratedWordHistory entity);
}
