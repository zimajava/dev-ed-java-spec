package org.zipli.socknet.reactive;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.exception.room.GetMessagesByRoomException;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IRoomHandler {

    Mono<ServerResponse> getRoom(ServerRequest request);

    Mono<ServerResponse> getRooms(ServerRequest request);

    Mono<ServerResponse> joinRoom(ServerRequest request);

    Mono<ServerResponse> leaveRoom(ServerRequest request);

    Mono<ServerResponse> deleteRoom(ServerRequest request);

    Mono<ServerResponse> createRoom(ServerRequest request);

    Mono<ServerResponse> saveMessage(ServerRequest request);

    Mono<ServerResponse> subscribeMessage(ServerRequest request);

    Mono<ServerResponse> getMessagesByRoom(ServerRequest request);
}
