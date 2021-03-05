package com.kenstevens.stratinit.client.server.rest;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WicketTest {
	@Test
    public void testAllWicketPanels() throws Exception {
    	ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AssignableTypeFilter(Panel.class));
        provider.addIncludeFilter(new AssignableTypeFilter(WebPage.class));
        provider.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Provider")));
        provider.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*ProviderImpl")));
 
        Set<BeanDefinition> components = provider.findCandidateComponents("com/kenstevens/stratinit/wicket");
        for (BeanDefinition component : components) {
            if (component.isAbstract()) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<MarkupContainer> clazz = (Class<MarkupContainer>) Class.forName(component.getBeanClassName());
            String resource = clazz.getSimpleName() + ".html";
            assertNotNull(clazz.getResourceAsStream(resource), "Resource [" + resource + "] not found with " + component.getBeanClassName());
        }
    }
}
