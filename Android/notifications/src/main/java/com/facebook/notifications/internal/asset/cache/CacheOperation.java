// Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
//
// You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
// copy, modify, and distribute this software in source code or binary form for use
// in connection with the web services and APIs provided by Facebook.
//
// As with any software that integrates with the Facebook platform, your use of
// this software is subject to the Facebook Developer Principles and Policies
// [http://developers.facebook.com/policy/]. This copyright notice shall be
// included in all copies or substantial portions of the software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.facebook.notifications.internal.asset.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

class CacheOperation implements ContentDownloader.DownloadCallback {
  private final @NonNull Object mutex;
  private final @NonNull Set<URL> urlsToCache;
  private final @NonNull Set<URL> remainingURLs;
  private final @NonNull ContentCache.CompletionCallback completion;

  public CacheOperation(@NonNull Set<URL> urlsToCache, @NonNull ContentCache.CompletionCallback completion) {
    this.mutex = new Object();
    this.urlsToCache = urlsToCache;
    this.remainingURLs = new HashSet<>(urlsToCache);
    this.completion = completion;
  }

  @NonNull
  public Set<URL> getUrlsToCache() {
    return urlsToCache;
  }

  @NonNull
  public ContentCache.CompletionCallback getCompletion() {
    return completion;
  }

  @Override
  public void onResourceDownloaded(@NonNull URL url, @Nullable File targetFile) {
    boolean invoke;
    synchronized (mutex) {
      remainingURLs.remove(url);
      invoke = remainingURLs.size() == 0;
    }

    if (invoke) {
      completion.onCacheCompleted(urlsToCache);
    }
  }
}
