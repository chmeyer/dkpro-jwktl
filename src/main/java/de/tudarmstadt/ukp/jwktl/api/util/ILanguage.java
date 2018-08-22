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
package de.tudarmstadt.ukp.jwktl.api.util;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryTranslation;

/**
 * Generic interface for languages used in Wiktionary. Instances of ILanguage
 * are used in as entry language of {@link IWiktionaryPage}s, the word 
 * language of {@link IWiktionaryEntry}s, and the target language 
 * of {@link IWiktionaryTranslation}s. Each language is encoded using the
 * international standard of language classification (ISO 639). Languages
 * are compared by their internal code.
 * @author Christian M. Meyer
 * @author Christof Müller
 * @author Lizhen Qu
 */
public interface ILanguage extends Comparable<ILanguage> {

	/** Returns the internal language code used by JWTKL. These codes roughly 
	 *  correspond to ISO 639-3, but also include language families, 
	 *  deprecated classifications, and not yet classified languages.
	 *  @return The internal language code used by JWTKL.
	 */
	String getCode();
	
	/** @return The language name (in English language). */
	String getName();
	
	/** @return The ISO 639-1 code or an empty string if none. */
	String getISO639_1();
	
	/** @return The ISO 639-2b code or an empty string if none. */
	String getISO639_2B();
	
	/** @return The ISO 639-2t code or an empty string if none. */
	String getISO639_2T();
	
	/** @return The ISO 639-3 code or an empty string if none. */
	String getISO639_3();
	
}
