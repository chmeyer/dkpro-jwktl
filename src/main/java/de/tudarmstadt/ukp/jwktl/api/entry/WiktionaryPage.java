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
package de.tudarmstadt.ukp.jwktl.api.entry;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.Language;

/**
 * Default implementation of the {@link IWiktionaryPage} interface.
 * See there for details.
 * @author Christian M. Meyer
 */
@Entity
public class WiktionaryPage implements IWiktionaryPage {
	
	@PrimaryKey
	protected long id;

	@SecondaryKey(relate = Relationship.ONE_TO_ONE)
	protected String title;
	@SecondaryKey(relate = Relationship.MANY_TO_ONE)
	protected String normalizedTitle;

	protected Date timestamp;
	protected long revision;
	protected String author;
	protected transient ILanguage entryLanguage;
	protected String entryLanguageStr;
	protected List<String> categories;
	protected Set<String> interWikiLinks;
	protected String redirectTarget;
	
	protected List<WiktionaryEntry> entries;

	/** Instanciates a new, empty page. */
	public WiktionaryPage() {
		entries = new ArrayList<>();
		categories = new ArrayList<>();
		interWikiLinks = new TreeSet<>();
	}
	
	/** Initialize the page and all of its entries. */
	public void init() {
		for (WiktionaryEntry entry : entries)
			entry.init(this);
	}

	/** Factory method for creating a new entry.
	 * @return New entry initialized with this page as the parent page; title of the pages is set as header of the entry.
	 */
	public WiktionaryEntry createEntry() {
		WiktionaryEntry result = new WiktionaryEntry();
		result.init(this);
		result.setHeader(title);
		return result;
	}
	
	
	// -- Identifier --
	
	public String getKey() {
		return Long.toString(id);
	}
	
	public long getId() {
		return id;
	}
	
	/** Assign the specified page ID.
	 * @param id numeric id of the page. 
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	
	// -- Page --
	
	public String getTitle() {
		return title;
	}

	/** Assigns the given title to this page.
	 * @param title title of the page.
	 */
	public void setTitle(final String title)  {
		this.title = title;
		this.normalizedTitle = normalizeTitle(title);
	}
			
	public Date getTimestamp() {
		return timestamp;
	}
	
	/** Assigns the given timestamp to this page.
	 * @param timestamp timestamp of the page.
	 */
	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getRevision() {
		return revision;
	}
	
	/** Assigns the given revision ID to this page.
	 * @param revision revision ID of the page.
	 */
	public void setRevision(long revision) {
		this.revision = revision;
	}
	
	public String getAuthor() {
		return author;
	}
	
	/** Assigns the given author name to this page.
	 * @param author author of the page.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public ILanguage getEntryLanguage() {
		if (entryLanguage == null && entryLanguageStr != null)
			entryLanguage = Language.get(entryLanguageStr);
		return entryLanguage;
	}
	
	/** Assigns the given entry language to this page.
	 * @param entryLanguage the language that this page is written in.
	 */
	public void setEntryLanguage(final ILanguage entryLanguage) {
		this.entryLanguage = entryLanguage;
		if (entryLanguage != null)
			entryLanguageStr = entryLanguage.getCode();
	}

	/** Add the given category to the list of categories.
	 *  @param category category of the page.
	 */
	public void addCategory(final String category) {
		categories.add(category);
	}
	
	public List<String> getCategories() {
		return categories;
	}

	/** Add the given interwiki link to the list of interwiki links.
	 * @param language link to other language editions of Wiktionary.
	 */
	public void addInterWikiLink(final String language) {
		interWikiLinks.add(language);
	}
	
	public Set<String> getInterWikiLinks() {
		return interWikiLinks;
	}

	public String getRedirectTarget() {
		return redirectTarget;
	}
	
	/** Assigns the given redirect target to this page.
	 * @param redirectTarget page title that a redirect page targets at.
	 */
	public void setRedirectTarget(final String redirectTarget) {
		this.redirectTarget = redirectTarget;
	}
	
	
	// -- Entries --
	
	/** Add the given entry to the list of entries.
	 * @param entry entry.
	 */
	public void addEntry(WiktionaryEntry entry) {
		entry.index = entries.size();
		entry.setId(entry.getIndex());
		entries.add(entry);
	}
	
	public WiktionaryEntry getEntry(int index) {
		return entries.get(index);
	}
	
	public int getEntryCount() {
		return entries.size();
	}
	
	public List<? extends IWiktionaryEntry> getEntries() {
		return entries;
	}
	
	/** Internal interface that is used by the parsers.
	 * @return The list of all {@link WiktionaryEntry}s.
	 */
	public List<WiktionaryEntry> entries()  {
		return entries;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ":" + id + ":" + title;
	}

	
	// -- Normalize --

	/** Static helper method for normalizing the title. That is, the title
	 *  is converted into lower case and non-ASCII characters are removed.
	 *  @param title title to normalize.
	 *  @return Normalized title.
	 */
	public static String normalizeTitle(final String title) {
		if (title == null)
			return null;
		
		return Normalizer.normalize(title, Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "")
				.toLowerCase(Locale.US);
	}

}
