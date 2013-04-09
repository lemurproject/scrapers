(ns nabble-scraper.core
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]))

(defn extract-forum-links
  [page-html]
  (map second (filter
					(fn
						[x]
					  	(= (first x) "forum"))
					(map 
					   	(fn [site-row-contents] 
					      	(let [contents (html/select site-row-contents #{[:div.tag] [:div.second-font :a]})
					            webapp-type (first (:content (first contents)))
					            link (:href (:attrs (second contents)))]
					    		[webapp-type link]))        
					   	(html/select page-html [:div.site-row])))))

(defn -main
  [& args]
  (let [[args [warc-file-name] banner] (cli/cli args)]
    (doseq [record (warc/get-http-records-seq (warc/get-warc-reader warc-file-name))]
      		(doseq [link (extract-forum-links (html/html-resource (io/reader (:payload-stream record))))]
          			(println link)))))