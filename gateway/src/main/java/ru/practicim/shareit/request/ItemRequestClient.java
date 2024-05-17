package ru.practicim.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicim.shareit.client.BaseClient;
import ru.practicim.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemRequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getPaginatedRequests(long userId, int from, int size) {
        Map<String, Object> params = Map.of(
            "from", from,
            "size", size
        );
        return get("/all?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}
