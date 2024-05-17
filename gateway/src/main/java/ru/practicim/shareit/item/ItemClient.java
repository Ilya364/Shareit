package ru.practicim.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicim.shareit.client.BaseClient;
import ru.practicim.shareit.comment.CommentDto;
import ru.practicim.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(long itemId, long userId, ItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public void deleteById(long userId, long itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchByText(long userId, String text) {
        Map<String, Object> parameters = Map.of(
            "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
