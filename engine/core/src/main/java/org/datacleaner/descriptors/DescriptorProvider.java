/**
 * DataCleaner (community edition)
 * Copyright (C) 2014 Neopost - Customer Information Management
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.datacleaner.descriptors;

import java.util.Collection;

import org.datacleaner.api.Alias;
import org.datacleaner.api.Analyzer;
import org.datacleaner.api.Filter;
import org.datacleaner.api.Renderer;
import org.datacleaner.api.RenderingFormat;
import org.datacleaner.api.Transformer;
import org.datacleaner.job.AnalysisJob;

/**
 * An interface for an object that provide descriptors for components such as
 * {@link Analyzer}, {@link Transformer}s and {@link Filter}s.
 * 
 * The {@link DescriptorProvider} is used heavily when reading/parsing
 * {@link AnalysisJob}s coming from e.g. XML files or other serialized formats.
 * 
 * Many of the descriptors provided by this class can also be generated by the
 * {@link Descriptors} helper methods. It is however advised to use the
 * {@link DescriptorProvider} since it keeps a proper registry of descriptors
 * used, and reuses existing descriptor definitions.
 */
public interface DescriptorProvider {

    /**
     * Gets all the {@link AnalyzerBeanDescriptor}s currently registered.
     * 
     * @return
     */
    public Collection<AnalyzerBeanDescriptor<?>> getAnalyzerBeanDescriptors();

    /**
     * Gets a {@link AnalyzerBeanDescriptor} by its {@link Analyzer} class.
     * 
     * @param analyzerClass
     * @return
     */
    public <A extends Analyzer<?>> AnalyzerBeanDescriptor<A> getAnalyzerBeanDescriptorForClass(Class<A> analyzerClass);

    /**
     * Gets a {@link AnalyzerBeanDescriptor} by its display name (or secondly by
     * searching using {@link Alias}es).
     * 
     * @param name
     * @return
     */
    public AnalyzerBeanDescriptor<?> getAnalyzerBeanDescriptorByDisplayName(String name);

    /**
     * Gets all {@link TransformerBeanDescriptor}s currently registered.
     * 
     * @return
     */
    public Collection<TransformerBeanDescriptor<?>> getTransformerBeanDescriptors();

    /**
     * Gets a {@link TransformerBeanDescriptor} by its {@link Transformer}
     * class.
     * 
     * @param transformerClass
     * @return
     */
    public <T extends Transformer> TransformerBeanDescriptor<T> getTransformerBeanDescriptorForClass(
            Class<T> transformerClass);

    /**
     * Gets a {@link TransformerBeanDescriptor} by its display name (or secondly
     * by searching using {@link Alias}es).
     * 
     * @param name
     * @return
     */
    public TransformerBeanDescriptor<?> getTransformerBeanDescriptorByDisplayName(String name);

    /**
     * Gets all {@link FilterBeanDescriptor}s currently registered.
     * 
     * @return
     */
    public Collection<FilterBeanDescriptor<?, ?>> getFilterBeanDescriptors();

    /**
     * Gets a {@link FilterBeanDescriptor} by its {@link Filter} class.
     * 
     * @param filterClass
     * @return
     */
    public <F extends Filter<C>, C extends Enum<C>> FilterBeanDescriptor<F, C> getFilterBeanDescriptorForClass(
            Class<F> filterClass);

    /**
     * Gets a {@link FilterBeanDescriptor} by its display name (or secondly by
     * searching using {@link Alias}es).
     * 
     * @param name
     * @return
     */
    public FilterBeanDescriptor<?, ?> getFilterBeanDescriptorByDisplayName(String name);

    /**
     * Gets all {@link RendererBeanDescriptor}s currently registered.
     * 
     * @return
     */
    public Collection<RendererBeanDescriptor<?>> getRendererBeanDescriptors();

    /**
     * Gets a {@link RendererBeanDescriptor} by its {@link Renderer} class.
     * 
     * @param rendererBeanClass
     * @return
     */
    public <R extends Renderer<?, ?>> RendererBeanDescriptor<R> getRendererBeanDescriptorForClass(
            Class<R> rendererBeanClass);

    /**
     * Gets all {@link RendererBeanDescriptor}s for a specific
     * {@link RenderingFormat}.
     * 
     * @param renderingFormat
     * @return
     */
    public Collection<RendererBeanDescriptor<?>> getRendererBeanDescriptorsForRenderingFormat(
            Class<? extends RenderingFormat<?>> renderingFormat);
}
