/*
 * Copyright 2011-2013 HTTL Team.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package httl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import httl.Context;
import httl.Engine;
import httl.Template;
import httl.internal.util.ClassUtils;
import httl.internal.util.IOUtils;
import httl.internal.util.StringUtils;
import httl.internal.util.UnsafeByteArrayOutputStream;
import httl.spi.Codec;
import httl.spi.Loader;
import httl.spi.loaders.ClasspathLoader;
import httl.spi.loaders.MultiLoader;
import httl.spi.parsers.templates.AdaptiveTemplate;
import httl.test.model.Book;
import httl.test.model.Model;
import httl.test.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * TemplateTest
 * 
 * @author Liang Fei (liangfei0201 AT gmail DOT com)
 */
@RunWith(Parameterized.class) 
public class TemplateTest {

    @Parameters
    public static Collection<Object[]> prepareData() throws Exception {  
    	List<Object[]> tests = new ArrayList<Object[]>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone("+0"));
		User user = new User("liangfei", "admin", "Y");
		Book[] books = new Book[10];
		books[0] = new Book("Practical API Design", "Jaroslav Tulach", "Apress", format.parse("2008-07-29"), 75, 85);
		books[1] = new Book("Effective Java", "Joshua Bloch", "Addison-Wesley Professional", format.parse("2008-05-28"), 55, 70);
		books[2] = new Book("Java Concurrency in Practice", "Doug Lea", "Addison-Wesley Professional", format.parse("2006-05-19"), 60, 60);
		books[3] = new Book("Java Programming Language", "James Gosling", "Prentice Hall", format.parse("2005-08-27"), 65, 75);
		books[4] = new Book("Domain-Driven Design", "Eric Evans", "Addison-Wesley Professional", format.parse("2003-08-30"), 70, 80);
		books[5] = new Book("Agile Project Management with Scrum", "Ken Schwaber", "Microsoft Press", format.parse("2004-03-10"), 40, 80);
		books[6] = new Book("J2EE Development without EJB", "Rod Johnson", "Wrox", format.parse("2011-09-17"), 40, 70);
		books[7] = new Book("Design Patterns", "Erich Gamma", "Addison-Wesley Professional", format.parse("1994-11-10"), 60, 80);
		books[8] = new Book("Agile Software Development, Principles, Patterns, and Practices", " Robert C. Martin", "Prentice Hall", format.parse("2002-10-25"), 80, 75);
		books[9] = new Book("Design by Contract, by Example", "Richard Mitchell", "Addison-Wesley Publishing Company", format.parse("2001-10-22"), 50, 85);
		Book[] books2 = new Book[2];
		books2[0] = new Book("Practical API Design2", "Jaroslav Tulach", "Apress", format.parse("2010-07-29"), 75, 85);
		books2[1] = new Book("Effective Java2", "Joshua Bloch", "Addison-Wesley Professional", format.parse("2010-05-28"), 55, 70);
		Map<String, Book> bookmap = new TreeMap<String, Book>();
		Map<String, Map<String, Object>> mapbookmap = new TreeMap<String, Map<String, Object>>();
		List<Map<String, Object>> mapbooklist = new ArrayList<Map<String, Object>>();
		for (Book book : books) {
			bookmap.put(book.getTitle().replaceAll("\\s+", ""), book);
			Map<String, Object> genericBook = ClassUtils.getProperties(book);
			mapbookmap.put(book.getTitle().replaceAll("\\s+", ""), genericBook);
			mapbooklist.add(genericBook);
		}
		Map<String, Book> bookmap2 = new TreeMap<String, Book>();
		for (Book book : books2) {
			bookmap2.put(book.getTitle().replaceAll("\\s+", ""), book);
		}
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("chinese", "中文");
		context.put("impvar", "abcxyz");
		context.put("defvar", "mnnm");
		context.put("html", "<a href=\"foo.html\">foo</a>");
		context.put("user", user);
		context.put("books", books);
		context.put("booklist", Arrays.asList(books));
		context.put("bookmap", bookmap);
		context.put("mapbookmap", mapbookmap);
		context.put("mapbooklist", mapbooklist);
		context.put("emptybooks", new Book[0]);
		context.put("books2", books2);
		context.put("booklist2", Arrays.asList(books2));
		context.put("bookmap2", bookmap2);
		Model model = new Model();
		model.setChinese("中文");
		model.setImpvar("abcxyz");
		model.setDefvar("mnnm");
		model.setHtml("<a href=\"foo.html\">foo</a>");
		model.setUser(user);
		model.setBooks(books);
		model.setBooklist(Arrays.asList(books));
		model.setBookmap(bookmap);
		model.setMapbookmap(mapbookmap);
		model.setMapbooklist(mapbooklist);
		model.setEmptybooks(new Book[0]);
		model.setBooks2(books2);
		model.setBooklist2(Arrays.asList(books2));
		model.setBookmap2(bookmap2);
		String[] configs = new String[] { "httl-comment.properties", "httl-comment-text.properties", "httl-comment-javassist.properties", "httl-attribute.properties" };
		for (String config : configs) {
			Engine engine = Engine.getEngine(config);
			Codec[] codecs = engine.getProperty("codecs", Codec[].class);
			String json = codecs[0].toString("context", context);
			Object[] maps = new Object[] {context, model, json, null};
			for (Object map : maps) {
				String dir = engine.getProperty("template.directory", "");
				if (dir.length() > 0 && dir.startsWith("/")) {
					dir = dir.substring(1);
				}
				if (dir.length() > 0 && ! dir.endsWith("/")) {
					dir += "/";
				}
				File directory = new File(TemplateTest.class.getClassLoader().getResource(dir + "templates/").getFile());
				assertTrue(directory.isDirectory());
				File[] files = directory.listFiles();
				for (int i = 0, n = files.length; i < n; i ++) {
					File file = files[i];
					tests.add(new Object[] {config, map, file.getName()});
				}
			}
		} 
        return tests;  
    }

	private String config;
	
	private Object map;
    
    private String templateName;

    public TemplateTest(String config, Object map, String templateName) {
		this.config = config;
		this.map = map;
		this.templateName = templateName;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTemplate() throws Exception {
		boolean profile = "true".equals(System.getProperty("profile"));
		String include = System.getProperty("includes");
		String exclude = System.getProperty("excludes");
		Set<String> includes = new HashSet<String>();
		Set<String> excludes = new HashSet<String>();
		if (StringUtils.isNotEmpty(include) && ! include.startsWith("$")) {
			includes.addAll(Arrays.asList(include.split("\\,")));
		} else if (StringUtils.isNotEmpty(exclude) && ! exclude.startsWith("$")) {
			excludes.addAll(Arrays.asList(exclude.split("\\,")));
		}
		Engine engine = Engine.getEngine(config);
		if (! profile) {
			Loader loader = engine.getProperty("loader", Loader.class);
			assertEquals(MultiLoader.class, loader.getClass());
			Loader[] loaders = engine.getProperty("loaders", Loader[].class);
			assertEquals(ClasspathLoader.class, loaders[0].getClass());
			loader = engine.getProperty("loaders", ClasspathLoader.class);
			assertEquals(ClasspathLoader.class, loader.getClass());
			String[] suffixes = engine.getProperty("template.suffix", new String[] { ".httl" });
			List<String> list = loader.list(suffixes[0]);
			assertTrue(list.size() > 0);
		}
		String dir = engine.getProperty("template.directory", "");
		if (dir.length() > 0 && dir.startsWith("/")) {
			dir = dir.substring(1);
		}
		if (dir.length() > 0 && ! dir.endsWith("/")) {
			dir += "/";
		}
		long max = profile ? Long.MAX_VALUE : 1;
		for (long m = 0; m < max; m ++) {
			//if (! "switch_filter.httl".equals(templateName)) continue; // 指定模板测试
			if ("httl-javassist.properties".equals(config)  // FIXME javassist的foreach 1..3编译不过
					&& "list.httl".equals(templateName)) continue;
			if (! profile)
				System.out.println(config + ": " + (map == null ? "null" : map.getClass().getSimpleName()) + " => " + templateName);
			if (excludes.contains(templateName) || 
					(includes.size() > 0 && ! includes.contains(templateName))) {
				continue;
			}
			String encoding = "UTF-8";
			if ("gbk.httl".equals(templateName)) {
				encoding = "GBK";
			}
			Engine _engine = engine;
			if ("extends_default.httl".equals(templateName)) {
				_engine = Engine.getEngine("httl-comment-extends.properties");
			}
			Template template = _engine.getTemplate("/templates/" + templateName, Locale.CHINA, encoding);
			if (! profile) {
				assertEquals(AdaptiveTemplate.class, template.getClass());
				assertEquals(Locale.CHINA, template.getLocale());
			}
			UnsafeByteArrayOutputStream actualStream = new UnsafeByteArrayOutputStream();
			StringWriter actualWriter = new StringWriter();
			if ("extends_var.httl".equals(templateName)) {
				if (map instanceof Map) {
					((Map<String, Object>) map).put("extends", "default.httl");
				} else if (map instanceof Model) {
					((Model) map).setExtends("default.httl");
				}
			}
			try {
				template.render(map, actualWriter);
				template.render(map, actualStream);
			} catch (Throwable e) {
				System.out.println("\n================================\n" + template.getCode() + "\n================================\n");
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage() + "\n================================\n" + template.getCode() + "\n================================\n", e);
			}
			if ("extends_var.httl".equals(templateName)) {
				if (map instanceof Map) {
					((Map<String, Object>) map).remove("extends");
				} else if (map instanceof Model) {
					((Model) map).setExtends(null);
				}
			}
			if (! profile && map != null) {
				URL url = this.getClass().getClassLoader().getResource(dir + "results/" + templateName + ".txt");
				if (url == null) {
					throw new FileNotFoundException("Not found file: " + dir + "results/" + templateName + ".txt");
				}
				File result = new File(url.getFile());
				if (! result.exists()) {
					throw new FileNotFoundException("Not found file: " + result.getAbsolutePath());
				}
				String expected = IOUtils.readToString(new InputStreamReader(new FileInputStream(result), encoding));
				expected = expected.replace("\r", "");
				if ("httl-comment-text.properties".equals(config) 
						&& ! "comment_cdata_escape.httl".equals(templateName)
						&& ! template.getSource().contains("read(")) {
					expected = expected.replace("<!--", "").replace("-->", "");
				}
				assertEquals(templateName, expected, actualWriter.getBuffer().toString().replace("\r", ""));
				assertEquals(templateName, expected, new String(actualStream.toByteArray()).replace("\r", ""));
				if ("set_parameters.httl".equals(templateName)) {
					assertEquals(templateName, "abc", Context.getContext().get("title"));
				}
			}
		}
		if (profile) {
			synchronized (TemplateTest.class) {
				try {
					TemplateTest.class.wait(20);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}