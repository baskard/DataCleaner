/**
 * eobjects.org DataCleaner
 * Copyright (C) 2010 eobjects.org
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
package org.eobjects.datacleaner.widgets.properties;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eobjects.analyzer.beans.StringAnalyzer;
import org.eobjects.analyzer.configuration.AnalyzerBeansConfiguration;
import org.eobjects.analyzer.configuration.AnalyzerBeansConfigurationImpl;
import org.eobjects.analyzer.data.ELInputColumn;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.descriptors.ConfiguredPropertyDescriptor;
import org.eobjects.analyzer.job.builder.AnalysisJobBuilder;
import org.eobjects.analyzer.job.builder.AnalyzerJobBuilder;
import org.eobjects.datacleaner.actions.AddExpressionBasedColumnActionListener;
import org.eobjects.datacleaner.actions.ReorderColumnsActionListener;
import org.eobjects.datacleaner.widgets.DCCheckBox;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.MutableColumn;
import org.eobjects.metamodel.util.CollectionUtils;

public class MultipleInputColumnsPropertyWidgetTest extends TestCase {

	public void testDefaultSelectAll() throws Exception {
		AnalyzerBeansConfiguration configuration = new AnalyzerBeansConfigurationImpl();

		AnalysisJobBuilder ajb = new AnalysisJobBuilder(configuration);
		ajb.addSourceColumn(new MutableColumn("foo", ColumnType.VARCHAR));
		MutableColumn barColumn = new MutableColumn("bar", ColumnType.VARCHAR);
		ajb.addSourceColumn(barColumn);
		ajb.addSourceColumn(new MutableColumn("baz", ColumnType.INTEGER));

		AnalyzerJobBuilder<StringAnalyzer> beanJobBuilder = ajb.addAnalyzer(StringAnalyzer.class);
		ConfiguredPropertyDescriptor property = beanJobBuilder.getDescriptor().getConfiguredPropertiesForInput().iterator()
				.next();

		MultipleInputColumnsPropertyWidget widget = new MultipleInputColumnsPropertyWidget(beanJobBuilder, property);

		// initialize with null (then select all)
		widget.initialize(null);
		InputColumn<?>[] value = widget.getValue();
		assertEquals("[MetaModelInputColumn[foo], MetaModelInputColumn[bar]]", Arrays.toString(value));

		// add another available column
		ajb.addSourceColumn(new MutableColumn("foobar", ColumnType.VARCHAR));

		value = widget.getValue();
		assertEquals("[MetaModelInputColumn[foo], MetaModelInputColumn[bar]]", Arrays.toString(value));
		assertEquals("foo,bar,foobar", getAvailableCheckBoxValues(widget));

		// remove a column
		ajb.removeSourceColumn(barColumn);

		value = widget.getValue();
		assertEquals("[MetaModelInputColumn[foo]]", Arrays.toString(value));
		assertEquals("foo,foobar", getAvailableCheckBoxValues(widget));
	}

	public void testAddAndRemoveExpressionColumn() throws Exception {
		AnalyzerBeansConfiguration configuration = new AnalyzerBeansConfigurationImpl();

		AnalysisJobBuilder ajb = new AnalysisJobBuilder(configuration);
		ajb.addSourceColumn(new MutableColumn("foo", ColumnType.VARCHAR));
		ajb.addSourceColumn(new MutableColumn("bar", ColumnType.VARCHAR));

		AnalyzerJobBuilder<StringAnalyzer> beanJobBuilder = ajb.addAnalyzer(StringAnalyzer.class);
		ConfiguredPropertyDescriptor property = beanJobBuilder.getDescriptor().getConfiguredPropertiesForInput().iterator()
				.next();

		// initialize with a expression column
		MultipleInputColumnsPropertyWidget widget = new MultipleInputColumnsPropertyWidget(beanJobBuilder, property);
		InputColumn<?>[] value = new InputColumn[] { new ELInputColumn("Hello #{name}") };
		widget.initialize(value);

		value = widget.getValue();
		assertEquals("[ELInputColumn[Hello #{name}]]", Arrays.toString(value));
		assertEquals("\"Hello #{name}\",foo,bar", getAvailableCheckBoxValues(widget));

		// select another column
		widget.setValue(new InputColumn[] { ajb.getSourceColumnByName("bar") });

		value = widget.getValue();
		assertEquals("[MetaModelInputColumn[bar]]", Arrays.toString(value));
		assertEquals("\"Hello #{name}\",foo,bar", getAvailableCheckBoxValues(widget));

		// simulate clicking the EL button
		AddExpressionBasedColumnActionListener.forMultipleColumns(widget).addExpressionBasedInputColumn("Hi #{nickname}");

		value = widget.getValue();
		assertEquals("[MetaModelInputColumn[bar], ELInputColumn[Hi #{nickname}]]", Arrays.toString(value));
		assertEquals("\"Hello #{name}\",foo,bar,\"Hi #{nickname}\"", getAvailableCheckBoxValues(widget));

		// add the same expression two times!
		AddExpressionBasedColumnActionListener.forMultipleColumns(widget).addExpressionBasedInputColumn("Hi #{nickname}");
		value = widget.getValue();
		assertEquals("[MetaModelInputColumn[bar], ELInputColumn[Hi #{nickname}]]", Arrays.toString(value));
		assertEquals("\"Hello #{name}\",foo,bar,\"Hi #{nickname}\"", getAvailableCheckBoxValues(widget));
	}

	public void testInitializeReordered() throws Exception {
		AnalyzerBeansConfiguration configuration = new AnalyzerBeansConfigurationImpl();

		AnalysisJobBuilder ajb = new AnalysisJobBuilder(configuration);
		ajb.addSourceColumn(new MutableColumn("foo", ColumnType.VARCHAR));
		ajb.addSourceColumn(new MutableColumn("bar", ColumnType.VARCHAR));
		ajb.addSourceColumn(new MutableColumn("baz", ColumnType.NVARCHAR));

		AnalyzerJobBuilder<StringAnalyzer> beanJobBuilder = ajb.addAnalyzer(StringAnalyzer.class);
		ConfiguredPropertyDescriptor property = beanJobBuilder.getDescriptor().getConfiguredPropertiesForInput().iterator()
				.next();

		// initialize with "baz" + "foo"
		MultipleInputColumnsPropertyWidget widget = new MultipleInputColumnsPropertyWidget(beanJobBuilder, property);
		InputColumn<?>[] value = new InputColumn[] { ajb.getSourceColumnByName("baz"), ajb.getSourceColumnByName("foo") };
		widget.initialize(value);

		assertEquals("baz,foo,bar", getAvailableCheckBoxValues(widget));
		assertEquals("[MetaModelInputColumn[baz], MetaModelInputColumn[foo]]", Arrays.toString(widget.getValue()));
	}

	public void testReorderColumns() throws Exception {
		AnalyzerBeansConfiguration configuration = new AnalyzerBeansConfigurationImpl();

		AnalysisJobBuilder ajb = new AnalysisJobBuilder(configuration);
		ajb.addSourceColumn(new MutableColumn("foo", ColumnType.VARCHAR));
		ajb.addSourceColumn(new MutableColumn("bar", ColumnType.VARCHAR));
		ajb.addSourceColumn(new MutableColumn("baz", ColumnType.LONGVARCHAR));

		AnalyzerJobBuilder<StringAnalyzer> beanJobBuilder = ajb.addAnalyzer(StringAnalyzer.class);
		ConfiguredPropertyDescriptor property = beanJobBuilder.getDescriptor().getConfiguredPropertiesForInput().iterator()
				.next();

		// initialize with all 3 columns + an expression
		MultipleInputColumnsPropertyWidget widget = new MultipleInputColumnsPropertyWidget(beanJobBuilder, property);
		InputColumn<?>[] value = CollectionUtils.array(ajb.getSourceColumns().toArray(new InputColumn[0]),
				new ELInputColumn("Hello #{name}"));
		widget.initialize(value);

		assertEquals("foo,bar,baz,\"Hello #{name}\"", getAvailableCheckBoxValues(widget));

		ArrayList<InputColumn<?>> reorderedValue = new ArrayList<InputColumn<?>>(Arrays.asList(value));
		reorderedValue.add(1, reorderedValue.remove(3));

		new ReorderColumnsActionListener(widget).saveReorderedValue(reorderedValue);

		assertEquals("foo,\"Hello #{name}\",bar,baz", getAvailableCheckBoxValues(widget));
		assertEquals(
				"[MetaModelInputColumn[foo], ELInputColumn[Hello #{name}], MetaModelInputColumn[bar], MetaModelInputColumn[baz]]",
				Arrays.toString(widget.getValue()));
	}

	/**
	 * Helper method to determine which checkboxes are shown
	 * 
	 * @param widget
	 * @return
	 */
	private String getAvailableCheckBoxValues(MultipleInputColumnsPropertyWidget widget) {
		StringBuilder sb = new StringBuilder();
		Component[] components = widget.getWidget().getComponents();
		for (Component component : components) {
			if (component instanceof DCCheckBox) {
				@SuppressWarnings("unchecked")
				DCCheckBox<InputColumn<?>> checkBox = (DCCheckBox<InputColumn<?>>) component;
				String name = checkBox.getValue().getName();
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(name);
			}
		}
		return sb.toString();
	}
}