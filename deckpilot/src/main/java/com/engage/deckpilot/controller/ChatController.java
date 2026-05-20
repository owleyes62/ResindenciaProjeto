package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.chat.ChatGeneratedDeckResponse;
import com.engage.deckpilot.dto.chat.ChatMessageCreateRequest;
import com.engage.deckpilot.dto.chat.ChatMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSendMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSessionCreateRequest;
import com.engage.deckpilot.dto.chat.ChatSessionResponse;
import com.engage.deckpilot.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatSessionResponse createSession(
            @RequestBody ChatSessionCreateRequest request
    ) {
        return chatService.createSession(request);
    }

    @GetMapping("/sessions")
    public Page<ChatSessionResponse> listSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatService.listSessions(page, size);
    }

    @GetMapping("/sessions/{sessionId}")
    public ChatSessionResponse findSessionById(@PathVariable Long sessionId) {
        return chatService.findSessionById(sessionId);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> listMessages(@PathVariable Long sessionId) {
        return chatService.listMessages(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ChatSendMessageResponse sendMessage(
            @PathVariable Long sessionId,
            @RequestBody @Valid ChatMessageCreateRequest request
    ) {
        return chatService.sendMessage(sessionId, request);
    }

    @GetMapping("/sessions/{sessionId}/generated-decks")
    public List<ChatGeneratedDeckResponse> listGeneratedDecks(
            @PathVariable Long sessionId
    ) {
        return chatService.listGeneratedDecks(sessionId);
    }

    @GetMapping("/sessions/{sessionId}/generated-decks/{generationIndex}")
    public ChatGeneratedDeckResponse findGeneratedDeckByIndex(
            @PathVariable Long sessionId,
            @PathVariable Integer generationIndex
    ) {
        return chatService.findGeneratedDeckByIndex(sessionId, generationIndex);
    }
}
