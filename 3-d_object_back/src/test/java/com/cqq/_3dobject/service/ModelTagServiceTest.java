package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ModelTag;
import com.cqq._3dobject.repository.ModelTagRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModelTagServiceTest {

    @Test
    void addTagsShouldReturnNullForEmptyInput() {
        ModelTagRepository repository = mock(ModelTagRepository.class);
        ModelTagService service = new ModelTagService();
        ReflectionTestUtils.setField(service, "repository", repository);

        List<ModelTag> result = service.addTags(11L, List.of());

        assertNull(result);
        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.<List<ModelTag>>any());
    }

    @Test
    void addTagsShouldMapEachNameToModelTag() {
        ModelTagRepository repository = mock(ModelTagRepository.class);
        when(repository.saveAll(org.mockito.ArgumentMatchers.<List<ModelTag>>any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ModelTagService service = new ModelTagService();
        ReflectionTestUtils.setField(service, "repository", repository);

        List<ModelTag> result = service.addTags(11L, List.of("古风", "青铜"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ModelTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(2, result.size());
        assertEquals(11L, captor.getValue().get(0).getMid());
        assertEquals("古风", captor.getValue().get(0).getTagName());
        assertEquals("青铜", captor.getValue().get(1).getTagName());
    }

    @Test
    void deleteTagsByMidShouldDelegateToRepository() {
        ModelTagRepository repository = mock(ModelTagRepository.class);
        ModelTagService service = new ModelTagService();
        ReflectionTestUtils.setField(service, "repository", repository);

        service.deleteTagsByMid(11L);

        verify(repository).deleteByMid(11L);
    }

    @Test
    void getTagsByMidShouldDelegateToRepository() {
        ModelTagRepository repository = mock(ModelTagRepository.class);
        ModelTag tag = new ModelTag(11L, "古风");
        when(repository.findByMid(anyLong())).thenReturn(List.of(tag));

        ModelTagService service = new ModelTagService();
        ReflectionTestUtils.setField(service, "repository", repository);

        List<ModelTag> tags = service.getTagsByMid(11L);

        assertEquals(1, tags.size());
        assertEquals("古风", tags.get(0).getTagName());
    }
}
