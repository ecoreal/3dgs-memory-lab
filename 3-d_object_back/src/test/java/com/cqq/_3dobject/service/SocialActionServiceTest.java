package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.SocialAction;
import com.cqq._3dobject.repository.SocialActionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SocialActionServiceTest {

    @Test
    void toggleActionShouldCreateRecordWhenMissing() {
        SocialActionRepository repository = mock(SocialActionRepository.class);
        when(repository.findByUidAndMidAndActionType(7L, 21L, SocialAction.ActionType.LIKE))
                .thenReturn(Optional.empty());

        SocialActionService service = new SocialActionService();
        ReflectionTestUtils.setField(service, "repository", repository);

        String result = service.toggleAction(7L, 21L, SocialAction.ActionType.LIKE);

        ArgumentCaptor<SocialAction> actionCaptor = ArgumentCaptor.forClass(SocialAction.class);
        verify(repository).save(actionCaptor.capture());
        SocialAction saved = actionCaptor.getValue();
        assertEquals("SUCCESS", result);
        assertEquals(7L, saved.getUid());
        assertEquals(21L, saved.getMid());
        assertEquals(SocialAction.ActionType.LIKE, saved.getActionType());
    }

    @Test
    void toggleActionShouldDeleteExistingRecord() {
        SocialActionRepository repository = mock(SocialActionRepository.class);
        when(repository.findByUidAndMidAndActionType(7L, 21L, SocialAction.ActionType.COLLECT))
                .thenReturn(Optional.of(new SocialAction(7L, 21L, SocialAction.ActionType.COLLECT)));

        SocialActionService service = new SocialActionService();
        ReflectionTestUtils.setField(service, "repository", repository);

        String result = service.toggleAction(7L, 21L, SocialAction.ActionType.COLLECT);

        assertEquals("CANCELLED", result);
        verify(repository).deleteByUidAndMidAndActionType(7L, 21L, SocialAction.ActionType.COLLECT);
        verify(repository, never()).save(any(SocialAction.class));
    }

    @Test
    void getCountShouldDelegateToRepository() {
        SocialActionRepository repository = mock(SocialActionRepository.class);
        when(repository.countByMidAndActionType(21L, SocialAction.ActionType.LIKE)).thenReturn(5L);

        SocialActionService service = new SocialActionService();
        ReflectionTestUtils.setField(service, "repository", repository);

        long count = service.getCount(21L, SocialAction.ActionType.LIKE);

        assertEquals(5L, count);
    }
}
