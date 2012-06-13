(ns stratinit-install.core
  (:require [clojure.string :as str])
  (:require [clojure.xml :as xml]))

(defn parse-file [filename]
    (first (xml-seq (xml/parse filename))))

(defn is-core [string]
  (.contains string "_core"))

(defn is-not-core-entry [entry]
  (not (is-core (-> entry :attrs :fileName))))

(defn filter-core-out [xml]
  (assoc xml :content (filter is-not-core-entry (:content xml))))

(defn fix-executable [exec]
  (str/replace
    (str/replace exec #"(.*)\.(.*)" "$1_core.$2")
    #"(.*)unix\d\d(.*)" "$1unix$2"))

(defn fix-executables [xml]
  (assoc xml :content
         (for [entry (:content xml)]
           (assoc-in entry [:attrs :fileName] (fix-executable (-> entry :attrs :fileName))))))
