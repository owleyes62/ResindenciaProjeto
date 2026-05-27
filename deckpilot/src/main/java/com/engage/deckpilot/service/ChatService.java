package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.chat.ChatGeneratedDeck;
import com.engage.deckpilot.domain.chat.ChatMessage;
import com.engage.deckpilot.domain.chat.ChatMessageRole;
import com.engage.deckpilot.domain.chat.ChatSession;
import com.engage.deckpilot.dto.chat.ChatGeneratedDeckResponse;
import com.engage.deckpilot.dto.chat.ChatMessageCreateRequest;
import com.engage.deckpilot.dto.chat.ChatMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSendMessageResponse;
import com.engage.deckpilot.dto.chat.ChatSessionCreateRequest;
import com.engage.deckpilot.dto.chat.ChatSessionResponse;
import com.engage.deckpilot.repository.ChatGeneratedDeckRepository;
import com.engage.deckpilot.repository.ChatMessageRepository;
import com.engage.deckpilot.repository.ChatSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatGeneratedDeckRepository chatGeneratedDeckRepository;
    private final DeckService deckService;
    private final MockDeckGenerationService mockDeckGenerationService;
    private final AiDeckGenerationService aiDeckGenerationService;

    @Transactional
    public ChatSessionResponse createSession(ChatSessionCreateRequest request) {
        ChatSession session = ChatSession.builder()
                .title(request.title())
                .build();

        ChatSession savedSession = chatSessionRepository.save(session);

        return toSessionResponse(savedSession);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> listSessions(int page, int size) {
        return chatSessionRepository.findAll(PageRequest.of(page, size))
                .map(this::toSessionResponse);
    }

    @Transactional(readOnly = true)
    public ChatSessionResponse findSessionById(Long sessionId) {
        ChatSession session = findSessionEntity(sessionId);

        return toSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> listMessages(Long sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Chat session not found with id: " + sessionId);
        }

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional
    public ChatSendMessageResponse sendMessage(Long sessionId, ChatMessageCreateRequest request) {
        ChatSession session = findSessionEntity(sessionId);

        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .role(ChatMessageRole.USER)
                .content(request.content())
                .build();

        ChatMessage savedUserMessage = chatMessageRepository.save(userMessage);

        boolean shouldGenerateDeck = request.content() != null
                && request.content().toLowerCase().contains("deck");

        if (!shouldGenerateDeck) {
            ChatMessage assistantMessage = ChatMessage.builder()
                    .session(session)
                    .role(ChatMessageRole.ASSISTANT)
                    .content("Mensagem recebida. Me diga qual tipo de deck você quer criar.")
                    .build();

            ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);

            return new ChatSendMessageResponse(
                    toMessageResponse(savedUserMessage),
                    toMessageResponse(savedAssistantMessage),
                    null
            );
        }

        ChatMessage assistantMessage = ChatMessage.builder()
                .session(session)
                .role(ChatMessageRole.ASSISTANT)
                .content("Gerando deck com IA...")
                .build();

        ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);

        AiDeckGenerationService.GeneratedDeckResult result =
                aiDeckGenerationService.generateDeckFromMessage(
                        session,
                        savedUserMessage,
                        savedAssistantMessage
                );

        savedAssistantMessage.setContent(result.assistantMessage());
        ChatMessage updatedAssistantMessage = chatMessageRepository.save(savedAssistantMessage);

        return new ChatSendMessageResponse(
                toMessageResponse(savedUserMessage),
                toMessageResponse(updatedAssistantMessage),
                toGeneratedDeckResponse(result.generatedDeck())
        );
    }

    @Transactional(readOnly = true)
    public List<ChatGeneratedDeckResponse> listGeneratedDecks(Long sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Chat session not found with id: " + sessionId);
        }

        return chatGeneratedDeckRepository.findBySessionIdOrderByGenerationIndexAsc(sessionId)
                .stream()
                .map(this::toGeneratedDeckResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatGeneratedDeckResponse findGeneratedDeckByIndex(
            Long sessionId,
            Integer generationIndex
    ) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Chat session not found with id: " + sessionId);
        }

        ChatGeneratedDeck generatedDeck = chatGeneratedDeckRepository
                .findBySessionIdAndGenerationIndex(sessionId, generationIndex)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Generated deck not found for session " + sessionId +
                                " and generation index " + generationIndex
                ));

        return toGeneratedDeckResponse(generatedDeck);
    }

    private ChatGeneratedDeckResponse toGeneratedDeckResponse(ChatGeneratedDeck generatedDeck) {
        Long userMessageId = generatedDeck.getUserMessage() == null
                ? null
                : generatedDeck.getUserMessage().getId();

        Long assistantMessageId = generatedDeck.getAssistantMessage() == null
                ? null
                : generatedDeck.getAssistantMessage().getId();

        return new ChatGeneratedDeckResponse(
                generatedDeck.getId(),
                generatedDeck.getSession().getId(),
                generatedDeck.getGenerationIndex(),
                deckService.toResponse(generatedDeck.getDeck()),
                userMessageId,
                assistantMessageId,
                generatedDeck.getCreatedAt()
        );
    }

    private ChatSession findSessionEntity(Long sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chat session not found with id: " + sessionId
                ));
    }

    private ChatSessionResponse toSessionResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getRole().name(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
