/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.jwktl.parser.de.components.nountable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryWordForm;
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryWordForm;
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext;
import de.tudarmstadt.ukp.jwktl.parser.util.PatternUtils;

public abstract class PatternBasedIndexedParameterHandler extends PatternBasedParameterHandler {

	protected final DEWordFormNounTableHandler nounTableHandler;

	public PatternBasedIndexedParameterHandler(DEWordFormNounTableHandler nounTableHandler, String regex) {
		super(regex);
		Objects.requireNonNull(nounTableHandler, "nounTableHandler must not be null.");
		this.nounTableHandler = nounTableHandler;
	}

	public void handle(String label, String value, WiktionaryWordForm wordForm, ParsingContext context) {
		final Matcher matcher = pattern.matcher(label);

		WiktionaryEntry wiktionaryEntry = context.findEntry();

		List<IWiktionaryWordForm> wordForms = wiktionaryEntry.getWordForms();
		final int indexOffset;
		if (wordForms == null) {
			indexOffset = 0;
		} else {
			final int maxInflectionGroup = wordForms.stream().mapToInt(IWiktionaryWordForm::getInflectionGroup).max()
					.orElse(0);
			indexOffset = (((maxInflectionGroup - 1) / DEWordFormNounTableHandler.MAX_INFLECTION_GROUP_COUNT) + 1)
					* DEWordFormNounTableHandler.MAX_INFLECTION_GROUP_COUNT;
		}

		if (matcher.find()) {
			final Integer index = PatternUtils.extractIndex(matcher);
			final int i = index == null ? 1 : index.intValue();
			handleIfFound(wordForm, label, i + indexOffset, value, matcher, context);
		}
	}

	public abstract void handleIfFound(WiktionaryWordForm wordForm, String label, int index, String value,
			Matcher matcher, ParsingContext context);

}
