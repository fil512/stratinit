(ns stratinit-install.test.run
  (:use [stratinit-install.core])
  (:require [clojure.string :as str]))

(def xml (parse-file "../stratinit-client/target/install4j/updates.xml"))

(def filtered-xml (filter-core-out xml))

(def final-xml (fix-executables filtered-xml))

(defn emit-element [e]
  (if (instance? String e)
    (println e)
    (do
      (print (str "<" (name (:tag e))))
      (when (:attrs e)
	(doseq [attr (:attrs e)]
	  (print (str " " (name (key attr)) "='" (val attr)"'"))))
      (if (:content e)
	(do
	  (println ">")
	  (doseq [c (:content e)]
	    (emit-element c))
	  (println (str "</" (name (:tag e)) ">")))
	(println "/>")))))

(defn emit [x]
  (println "<?xml version='1.0' encoding='UTF-8'?>")
  (emit-element x))

(with-open [w (clojure.java.io/writer  "../stratinit-client/target/updates.xml")]
  (.write w (with-out-str (emit final-xml))))
