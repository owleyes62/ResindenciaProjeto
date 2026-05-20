package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.chat.ChatGeneratedDeckResponse;
import com.engage.deckpilot.dto.chat.ChatMessageCreateRequest;
import com.engage.deckpilot.dto.chat.ChatMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSendMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSessionCreateRequest;
import com.engage.deckpilot.dto.chat.ChatSessionResponse;
import com.engage.deckpilot.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Endpoints para sessões de chat, mensagens e decks gerados")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Criar sessão de chat", description = "Cria uma nova conversa")
    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatSessionResponse createSession(
            @RequestBody ChatSessionCreateRequest request
    ) {
        return chatService.createSession(request);
    }

    @Operation(summary = "Listar sessões de chat", description = "Retorna sessões de chat com paginação")
    @GetMapping("/sessions")
    public Page<ChatSessionResponse> listSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatService.listSessions(page, size);
    }

    @Operation(summary = "Buscar sessão por ID", description = "Retorna uma sessão de chat específica")
    @GetMapping("/sessions/{sessionId}")
    public ChatSessionResponse findSessionById(@PathVariable Long sessionId) {
        return chatService.findSessionById(sessionId);
    }

    @Operation(summary = "Listar mensagens da sessão", description = "Retorna as mensagens de uma sessão em ordem cronológica")
    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> listMessages(@PathVariable Long sessionId) {
        return chatService.listMessages(sessionId);
    }

    @Operation(summary = "Enviar mensagem", description = "Salva uma mensagem do usuário e retorna uma resposta mockada do assistente")
    @PostMapping("/sessions/{sessionId}/messages")
    public ChatSendMessageResponse sendMessage(
            @PathVariable Long sessionId,
            @RequestBody @Valid ChatMessageCreateRequest request
    ) {
        return chatService.sendMessage(sessionId, request);
    }

    @Operation(summary = "Listar decks gerados", description = "Retorna os decks gerados dentro de uma sessão de chat")
    @GetMapping("/sessions/{sessionId}/generated-decks")
    public List<ChatGeneratedDeckResponse> listGeneratedDecks(
            @PathVariable Long sessionId
    ) {
        return chatService.listGeneratedDecks(sessionId);
    }

    @Operation(summary = "Buscar deck gerado por índice", description = "Retorna um deck gerado específico dentro de uma sessão")
    @GetMapping("/sessions/{sessionId}/generated-decks/{generationIndex}")
    public ChatGeneratedDeckResponse findGeneratedDeckByIndex(
            @PathVariable Long sessionId,
            @PathVariable Integer generationIndex
    ) {
        return chatService.findGeneratedDeckByIndex(sessionId, generationIndex);
    }
}
