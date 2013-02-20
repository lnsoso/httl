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
package httl.spi.interceptors;

import httl.Context;
import httl.spi.Interceptor;
import httl.spi.Listener;

import java.io.IOException;
import java.text.ParseException;

/**
 * TemplateInterceptor. (SPI, Singleton, ThreadSafe)
 * 
 * @see httl.spi.parsers.AbstractParser#setInterceptor(Interceptor)
 * 
 * @author Liang Fei (liangfei0201 AT gmail DOT com)
 */
public abstract class TemplateInterceptor implements Interceptor {

	public void render(Context context, Listener listener)
			throws IOException, ParseException {
		if (context.getTemplate().isMacro()) { 
			listener.render(context);
			return;
		}
		doRender(context, listener);
	}

	protected abstract void doRender(Context context, Listener listener)
			throws IOException, ParseException;

}
