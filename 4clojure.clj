(defn disjointX [x] (if (empty? x) true (and (

(fn disjointN [x y] (every? #(

(fn disjoint2 [x y] (not-any? (fn[a] (contains? y a)) x)) 

x %) y))

(first x)(rest x))(disjointX (rest x)))))


(defn reductions3 [op & x] 
(
let [reductions2 
(fn  [op reduction x]  
(lazy-seq (if (empty? x) [reduction] 
(let [newreduction (op reduction (first x))] 
(cons reduction 
(reductions2 op newreduction (rest x)))))))
]
(if (= (count x) 2)
(lazy-seq (reductions2 op (first x) (second x)))
(lazy-seq (reductions2 op (first (first x)) (rest (first x))))))
)


(defn mergekey [op k x y]
	(if (and (contains? x k)(contains? y k))
	 { k (op (get x k) (get y k) )}
	 (if (contains? x k)
	 	{k (get x k)  }
	 	{k (get y k)  }
	 )
	 )
)
(defn merge2 [op x y]
(let [ks (distinct (concat (keys x) (keys y)))]
(apply merge (map #(mergekey op % x y)  ks))
))

(defn mergeV [op x]
(if (= 1 (count x)) (first x)
	(merge2 op (first x)(mergeV op (rest x)))
))
(defn mergeX [op & x] (mergeV op x))


(defn anagrams? [x y] (= (sort x)(sort y)))

(defn anagrams1 [tst x] (conj (set (filter (fn [xi] (anagrams? tst xi)) x)) tst)) 

(defn anagramsRec [x res]
	(if (empty? x) res
	(let [
		v (anagrams1 (first x)(rest x))
		newX (remove v x)
		newRes (if (> (count v) 1) (conj res v) res)
		]
	(anagramsRec newX newRes)
	)))

(defn anagrams [x] (anagramsRec x #{}))


(fn anagramsB [x] ((fn anagramsRec [x res]
	(if (empty? x) res
	(let [
		v ((fn anagrams1 [tst x] (conj (set (filter (fn [xi] ((fn anagrams? [x y] (= (sort x)(sort y))) tst xi)) x)) tst))  (first x)(rest x))
		newX (remove v x)
		newRes (if (> (count v) 1) (conj res v) res)
		]
	(anagramsRec newX newRes)
	)))
 x #{}))

(defn camel [s] 
	(let [splitted (clojure.string/split s #"-")]
	(clojure.string/join (cons (first splitted)(map clojure.string/capitalize (rest splitted))))
	))

(defn gcd [a b]
	(if(zero? b) a
		(gcd b (mod a b))
	))

(defn coprime? [a b] (= (gcd a b) 1))

(defn totient [n] 
(if (= n 1) 1
	(count (filter (fn [x] (coprime? x n)) (range 1 n))))
)

(defn digits[n]
	(if (< n 10) (list n) 
		(cons (mod n 10) (digits (quot n 10)))))

(defn sumsquares [x] (reduce + (map (fn [n] (* n n)) x)))

(defn squaresDigits [n] (sumsquares (digits n)))

(defn happyRec [n seen] 
	(if (= n 1) true 
		(if (contains? seen n) false 
			(happyRec (squaresDigits n) (conj seen n))
			)))

(defn happy [n] (happyRec n #{}))

(defn balancedDigits[x] 
	(let [half (quot (count x) 2)
		firstPart (take half x)
		lastPart (take-last half x)
		]
		(= (reduce + firstPart)(reduce + lastPart))
		))

(defn balanced[n] (balancedDigits (digits n)))

(defn removeItem [x n] (filter #(not= % n) x))

(defn powerSetRec[res toAdd]
	(if (nil? (second toAdd)) #{res}
		(cons res (mapcat 
			(fn [n] (powerSetRec (conj res n) (disj toAdd n)))
		toAdd))
	))

(defn powerSet [s] (set (cons s (powerSetRec #{} s))))

(defn binToSubset [b sVec length] 
	(set (map (fn[n] (get sVec n))
		(filter (fn[n] (bit-test b n)) (range 0 length))
	)))

(defn powersetbin[s]
	(let [
		length (count s)
		binaryLength (int (Math/pow 2 length))
		allBinaries (range 0 binaryLength)
		sVec (vec s)
		]
		(set (map (fn [b] (binToSubset b sVec length)) allBinaries))
		)
)

(defn partitionKeyword[v] (partition-by (fn [n] (keyword? n)) v))


(defn partitionKeywordSimple[v] 
(mapcat
	(fn[n] (if (keyword? (first n)) n [n])) 
	(partitionKeyword v)
	)
)

(defn after2 [v] (rest (rest v)))


(defn keysAndValueRec[v res]
	(if (empty? v) res
		(if (= (count v) 1) (assoc res (first v) [])
			(if (keyword? (second v))
				(keysAndValueRec (rest v) (assoc res (first v) []))
				(keysAndValueRec (after2 v) (assoc res (first v) (second v)))
			)
		)
	)
)


(defn keysAndValue[v] (keysAndValueRec (partitionKeywordSimple v) {}))

(defn keysAndValueX[v] ((fn keysAndValueRec[v res]
	(if (empty? v) res
		(if (= (count v) 1) (assoc res (first v) [])
			(if (keyword? (second v))
				(keysAndValueRec (rest v) (assoc res (first v) []))
				(keysAndValueRec ((fn after2 [v] (rest (rest v))) v) (assoc res (first v) (second v)))
			)
		)
	)
) ((fn partitionKeywordSimple[v] 
(mapcat
	(fn[n] (if (keyword? (first n)) n [n])) 
	((fn partitionKeyword[v] (partition-by (fn [n] (keyword? n)) v)) v)
	)
) v) {}))


(defn partitionIdentity [v] (partition-by identity v))

(defn pronunciationOne [v] [(count v) (first v)])

(defn pronunciation [v] 
(mapcat pronunciationOne (partitionIdentity v))
)

(defn pronunciationRec [v]
	(lazy-seq (let [newV (pronunciation v)] 
		(cons newV (pronunciationRec newV))))
	)

(defn applyRec [i n fs fslength]
	(let [
		f (get fs (mod i fslength))
		newN (f n)
		]
		(lazy-seq (cons n (applyRec (inc i) newN fs fslength))
		)
))

(defn applyfs [n & fs] (applyRec 0 n (vec fs) (count (vec fs))))

(defn decurryOne [f parms] 
	(if (empty? parms) f
		(decurryOne (f (first parms)) (rest parms) )
	))

(defn decurryOneVarity [f & parms] (decurryOne f (vec parms))) 

(defn decurry [f] (partial decurryOneVarity f))

(defn maxOfMany[x] (reduce max x))
(defn dropWhileLessThan[x n] (drop-while #(< % n) x))
(defn dropWhileLessThanAll[xs n] (map (fn [x] (dropWhileLessThan x n)) xs))
(defn allEqualTo?[x n] (every? #(= n %) x))
(defn someEmpty?[xs] (some empty? xs))

(defn lazysearchRec[xs]
	(if(someEmpty?  xs) nil
		(let [
			firsts (map first xs)
			maxFirst (maxOfMany firsts)
			allMax? (allEqualTo? firsts maxFirst)
			]
				(if allMax? 
					maxFirst
					(lazysearchRec (dropWhileLessThanAll xs maxFirst))
				)
		)
	))

(defn lazysearch[& xs] (lazysearchRec (vec xs))) 

(defn noListInside? [x] (empty? (filter #(coll? %) x)))

(defn flattenRec [x] (if (noListInside? x) 
	[x]
	(mapcat flattenRec x)
	))
(defn flatten2 [x] (flattenRec x))

(defn splitBefore [x p] (split-with #(not (p %)) x))

(defn takeWhileRec [n p x res]
	(if 
		(or (empty? x) (zero? n)) 
		res
		(
			let 
			[
				c (first x)
				newX (rest x)
				newRes (cons c res)
			]
			(lazy-seq
				(if (p c)
					(takeWhileRec (dec n) p newX newRes)
					(takeWhileRec n p newX newRes)
				)						
			)
		)
	)
)

(defn takeWhileN [n p x] (reverse (rest (takeWhileRec n p x []))))

(defn insertBetweenRec [p i x]
	(if (empty? x)
		[]
		(if (nil? (second x))
			x
			(
				let
				[
					a (first x)
					b (second x)
					newX (rest x)
				]
				(if (p a b) 
					(lazy-seq (cons a (cons i (insertBetweenRec p i newX))))
					(lazy-seq (cons a (insertBetweenRec p i newX)) )
				)
			)
		)
	)
)

(defn mapRec [p x]
	(if (empty? x)
		[]
		(lazy-seq (cons (p (first x)) (mapRec p (rest x))))
	)
)

(defn funCompRec [fs x]
	(if (nil? (second fs))
		(apply (first fs) x)
		((first fs) (funCompRec (rest fs) x))
	)
)

(defn funCompP [fs & x] (funCompRec fs (vec x)))


(defn funComp [& fs] (partial funCompP (vec fs)))

(defn equivalentTo [f x n] 
	(let [fn (f n)]
		(conj (set (filter #(= (f %) fn) x)) n)
	)
)


(defn equivalence [f x] (equivalenceRec f x #{}))

(defn toRomansRec [n]
	(cond
		(>= n 1000) (cons "M" (toRomansRec (- n 1000)))
		(>= n 900) (cons "CM" (toRomansRec (- n 900)))
		(>= n 500) (cons "D" (toRomansRec (- n 500)))
		(>= n 400) (cons "CD" (toRomansRec (- n 400)))
		(>= n 100) (cons "C" (toRomansRec (- n 100)))
		(>= n 90) (cons "XC" (toRomansRec (- n 90)))
		(>= n 50) (cons "L" (toRomansRec (- n 50)))
		(>= n 40) (cons "XL" (toRomansRec (- n 40)))
		(>= n 10) (cons "X" (toRomansRec (- n 10)))
		(>= n 9) (cons "IX" (toRomansRec (- n 9)))
		(>= n 5) (cons "V" (toRomansRec (- n 5)))
		(>= n 4) (cons "IV" (toRomansRec (- n 4)))
		(>= n 1) (cons "I" (toRomansRec (- n 1)))
		:else [""]
	)
)

(defn toRomans [n] (clojure.string/join (toRomansRec n)))



(defn column [x y z n] [(get x n)(get y n)(get z n)])
(defn diagonalLeft [x y z] [(get x 0)(get y 1)(get z 2)])
(defn diagonalRight [x y z] [(get x 2)(get y 1)(get z 0)])

(defn all [x s] (every? #(= % s) x))

(defn ticTacToeSymbol [m s]
	(let 
		[
		x (get m 0)
		y (get m 1)
		z (get m 2)
		]
		
	(or
	(all x s)
	(all y s)
	(all z s)
	(all (column x y z 0) s)
	(all (column x y z 1) s)
	(all (column x y z 2) s)
	(all (diagonalLeft x y z) s)
	(all (diagonalRight x y z) s)
	))
)

(defn ticTacToe [m]
	(cond
		(ticTacToeSymbol m :x) :x
		(ticTacToeSymbol m :o) :o
		:else nil
	)
)

(defn trampRec [f]
	(let [res (f)]
		(if (not (fn? res))
			res
			(trampRec res)
		)
	)
)

(defn tramp [f & n]
	(let [res (apply f n)]
		(if (not (fn? res))
			res
			(trampRec res)
		)
	)
)

(defn roman1 [r]
	(case r
	\M 1000
	\D 500
	\C 100
	\L 50
	\X 10
	\V 5
	\I 1
	)
)

(defn romanRec [x]
	(if (empty? x)
		0
		(let [r1 (roman1 (first x))]
			(if (nil? (second x))
				r1
				(let [r2 (roman1 (second x))]
					(if (>= r1 r2)
						(+ (romanRec (rest x)) r1)
						(- (romanRec (rest x)) r1)
					)
				)
			)
		)
	)
)

(defn roman [s] (romanRec (seq s)))

(defn onlyPars[s] (clojure.string/join (filter #(contains? #{\( \) \[ \] \{ \}} %) (seq s))))
(defn removePars [s] (clojure.string/replace s #"\(\)|\[\]|\{\}" ""))

(defn parsRec[s]
	(let [newS (removePars s)]
		(if (empty? newS)
			true
			(if (= (count s)(count newS))
				false
				(parsRec newS)
			)
		)
	)
)

(defn pars[s] (parsRec (onlyPars s)))



(defn kombinations[x n]
	(let [
		length (count s)
		binaryLength (int (Math/pow 2 length))
		allBinaries (range 0 binaryLength)
		sVec (vec s)
		]
		(set (map (fn [b] (binToSubset b sVec length)) allBinaries))
		)
)






(defn withPrevious[x] (map (fn[a b] [a b]) x (drop 1 x)))

(defn inSuccession[x] (= 1(- (second x)(first x))))

(defn readSequence[x res]
	(if (empty? x)
		[(reverse res) x]
		(if (nil? (second x))
			[(reverse (cons (first x) res)) []]	
			(if (inSuccession x)
				(readSequence (rest x) (cons (first x)res))
				[(reverse (cons (first x) res)) (rest x)]
			)
		)
	)
)

(defn searchSequenceRec[x]
	(if (empty? x)
		nil
		(if (nil? (second x))
			[]
			(if(inSuccession x)
				(let [res (readSequence x [])]
					(cons (first res)(searchSequenceRec (second res)))
				)
				(searchSequenceRec (rest x))
			)
		)
	)
)


(defn maxSeq[a b] 
	(if (> (count a) (count b))
		a
		b
	)
)


(defn searchSequence[x] 
	(let [res (searchSequenceRec x)]
		(if (empty? res)
		[]
		(reduce maxSeq res)
		)
	)
)


(defn equivalent[t1 t2]
	(if (nil? t1)
		(nil? t2)		
		(and ( = (first t1) (first t2))
			(and 
				(equivalent (second t1)(last t2) )
				(equivalent (last t1)(second t2) )
			)
		)
	)
)



(defn symmetric[t]
	(equivalent (second t) (last t))
)


(defn flattenMap[m]
	 (apply merge (for [e1 (seq m) e2 (seq (second e1))]
				{[(first e1) (first e2)] (second e2)}
	))
)



(defn isPalindromicNumber [n l]
	(cond  
		(= l 0) true
		(= l 10) (= (quot n 10)(mod n 10))
		(= l 100) (= (quot n 100)(mod n 10))
		:else (and (= (quot n l)(mod n 10)) (isPalindromicNumber (quot (mod n l) 10) (quot l 100)  ))
	)	
)


(defn getNumberSize[n l m]
	(if (and (>= n l) (< n m))
		l
		(getNumberSize n m (* m 10))
	)
)


(defn palindromicsAfterRec[n l m]
	(lazy-seq 
		(if (< n m)
			(if (isPalindromicNumber n l)
				(cons n (palindromicsAfterRec (inc n) l m))
				(palindromicsAfterRec (inc n) l m)
			)
			(palindromicsAfterRec n m (* m 10))
		)
	)
)


(defn palindromicsAfter[n]
	(let [
		l (getNumberSize n 0 10)
		m (* l 10)
		]
		(palindromicsAfterRec  n l m)
	)
)

(defn tens[l]
	(case l 
		0 1
		1 10
		2 100
		3 1000
		4 10000
		(* 10 (tens (dec l)))
	)
)

(defn toDigitSeq[n]
	(
		(fn[n res]
			(if (< n 10)
				(cons n res)
				(recur (quot n 10) (cons (mod n 10) res))
			)
		)
		n []
	)
)


(defn fromDigitSeq[x]
	(
		(fn[x res]
			(if (empty? x)
				res
				(recur (rest x) (+ (* res 10) (first x)))
			)
		) x 0
	)
)

(defn mirrorOfSeq [s]
	(fromDigitSeq (concat s (reverse s)))
)

(defn mirrorOfSeq2 [s]
	(fromDigitSeq (concat s (rest (reverse s))))
)



(defn halfOrMore [n]
	(let [q (quot n 2)]
		(if (< (+ q q) n)
			(inc q)
			q
		)
	)
)

(defn buildStart[from l hm]
	(let [

			halfFromSeq (take hm (toDigitSeq from))
			wholeFrom (if(even? l) (mirrorOfSeq halfFromSeq) (mirrorOfSeq2 halfFromSeq))
			halfFrom (fromDigitSeq halfFromSeq) 
		]
		(if (>= wholeFrom from)
			halfFrom	
			(inc  halfFrom)
		)
	)
)

(defn numberLength[n] 
	(cond 
		(< n 10) 1
		(< n 100) 2
		(< n 1000) 3
		(< n 10000) 4
		(< n 100000) 5
		(< n 1000000) 6
		:else (inc  (numberLength (quot n 10)))
	)
)


(defn buildPalindromesRec[from l start]
	(lazy-seq 
		(let [
			hm (halfOrMore l)
			end (tens hm)
			lst (map toDigitSeq (range start end))
			]
			(concat 
				(if (even? l)
					(map mirrorOfSeq lst)
					(map mirrorOfSeq2 lst)
				) 
				(buildPalindromesRec (tens l) (inc l) (tens (dec (halfOrMore (inc l)))))
			)
		)
	)	
)

(defn buildPalindromes[from]
	(lazy-seq 
		(if (< from 10)
			(concat (range from 10) (buildPalindromesRec 10 2 1))
			(let [l (numberLength from)]				
				(buildPalindromesRec from l (buildStart from l (halfOrMore l)))
			)				
		)
	)
)


(defn removeI [v n]
	(vec (concat (subvec v 0 n) (subvec v (inc n))))
	)

(defn kCombinationRec [k v]
	(if (or (empty? v) (zero? k))
		[#{}]
		(let [
			indexes (range 0 (count v))
			newK (dec k)
			]
			(mapcat (fn[i] (map #(conj % (get v i)) (kCombinationRec newK (removeI v i)) )) indexes)
		)
	)
)

(defn kCombination [k s]
	(if (> k (count s)) 
		#{}
		(set (kCombinationRec k (vec s)))
	)
)

(defn calcRec[f v]
	(cond 
		(symbol? f) (get v f)
		(number? f) f
		:else (case (first f)
				/ (apply / (map #(calcRec % v) (rest f))) 
				+  (apply + (map #(calcRec % v)  (rest f)))
				-  (apply - (map #(calcRec % v)  (rest f)))
				*  (apply * (map #(calcRec % v)  (rest f)))				
			)
	)
)

(defn calc [f]
	(partial calcRec f)
)



(defn multiple? [n m] (zero? (mod m n)))

(defn isPrime[n]
	(and 
		(> n 1)
		(not-any? #(multiple? % n) (range 2 n))
	)
)

(defn primes[] (lazy-seq (filter #(isPrime %) (drop 2 (range)))))

(defn findPrimeBefore[n]
	(first (filter isPrime (range (dec n) 2 -1)))
)

(defn findPrimeAfter[n]
	(first (filter isPrime (drop (inc n) (range))))
)




(defn isBalancedPrime[n]
	(and
		(> n 3)
		(isPrime n)
		(= n(quot (+ (findPrimeBefore n)(findPrimeAfter n))  2))
	)
)


(defn sumOfFirstN [n]
	(quot (* n (inc n)) 2)
)

(defn countABefore [n a]
	(quot (dec n) a)
)


(defn bigDivideOne[n a]
	(* (sumOfFirstN (bigint (countABefore n a))) a)	
)

(defn bigDivide[n a b]
	(-
		(+
			(bigDivideOne n a)
			(bigDivideOne n b)
		)
		(bigDivideOne n (* a b))
	)
)

(defn intervalsRec[x b]
	(cond 
		(empty? x) [b]
		(= (last b)(first x)) (intervalsRec (rest x) b)		
		(= (inc (last b))(first x)) (intervalsRec (rest x) (conj b (first x)))
		:else (cons b (intervalsRec (rest x) [(first x)]))
	)
)

(defn toInterval[x]
	[(first x)(last x)]
)


(defn intervals[v]
	(if (empty? v) 
		[]
		(let [
			x (sort v)
			]
			(map toInterval (intervalsRec (rest x) [(first x)]))
		)
	)
)

(defn powerSetSums [s]
	(set (map #(reduce + %) (filter not-empty (powersetbin s))))
)

(defn sumSet [& s]
	(not (empty? (apply clojure.set/intersection (map powerSetSums s))))
)

(defn horribilisRec [m x sum]
	(if (empty? x)
		x
		(let [n (first x)]
			(if (number? n)
				(let [newSum (+ sum n)]
					(if (<=  newSum m)
						(cons n (horribilisRec m (rest x) newSum))
						(list)
					)
				)
				(list (horribilisRec m n sum))
			)
		)
	)	 
)

(defn horribilis[m x]
	(horribilisRec m x 0)
)

(defn winnerRank [c]
	(last (sort-by #(get % :rank) c))
)

(defn winnerCards [t c]
	(if (nil? t)
		(winnerCards (get (first c) :suit) c)
		(winnerRank (filter #(= (get % :suit) t) c))
	)
)

(defn winner [t]
	(partial winnerCards t)
)

(defn cell[i j f]
	(lazy-seq (cons (f i j) (cell i (inc j) f)))

)

(defn row[i n f]
	(lazy-seq (cons (cell i n f) (row (inc i) n f)))
)

(defn matrix 
	([f m n s t] (take s (map #(take t %) (row m n f))))
	([f m n] (row m n f))
	([f] (row 0 0 f))
)

(defn parDecorate[n]
	(str "(" n ")" ) 
)

(defn parDecorateAll[x]
	(set (map parDecorate x)) 
)



(defn combinations[s t]
	(for[i s j t]
		(str i j)
	)
)
(defn parCombinations[n m]
	(let [
		pn (pars n)
		pm (pars m)
		]
		(concat (combinations pn pm) (combinations pm pn))
	)	
)

(defn parCombinationsV[v]
	(parCombinations (get v 0)(get v 1))	
)


(defn couples[n]
	(set (map (comp vec sort vector) (range 1 n)(range (dec n) 0 -1)))
)


(defn parCouples[n]
	(set (mapcat parCombinationsV (couples n)))
)


(defn pars[l]
	(cond 
		(zero? l) (hash-set "")
		(= l 1) (hash-set "()")
		:else (clojure.set/union (parCouples l) (parDecorateAll(pars (dec l))))  
	)
)

(defn triangleRec [t i sum]
	(if (empty? t)
		sum
		(let [
			t0 (first t)
			ti (get t0 i)
			i1 (inc i)
			ti1 (get t0 i1) 
		]
			(min (triangleRec (rest t) i (+ sum ti)) (triangleRec (rest t) i1 (+ sum ti1)))
		)
	)
)

(defn triangle [t]
	(triangleRec (rest t) 0 (first (first t)) )

)

(defn different [ai bi]
	(= ai bi)
)

(defn substitution[a b]
	(and 
		(= (count a)(count b))
		(= (count (filter false? (map different a b))) 1)
	)
)

(defn removeS[s n]
	(str (subs s 0 n) (subs s (inc n)))
	)

(defn inserted [a b]
	(if
		(= (inc (count a))(count b))
		(let [idx (first (filter #(not= (get a %)(get b %)) (range (count b))))]
			(= a (removeS b idx))
		)
		false
	)
)	


(defn chained[a b]
	(or
		(substitution a b)
		(inserted a b)
		(inserted b a)
	)
)




(defn chainRec[w x]
	(if (empty? x)
		true
		(not (not-any? (fn[w1] (and (chained w w1) (chainRec w1 (disj x w1)))) x))
	)

)
(defn chain[x]
	(not (not-any?  (fn[w] (chainRec w (disj x w))) x))
)

(defn mazeDouble[a]
	(+ a a)
)

(defn mazeAdd2[a]
	(+ a 2)
)
(defn mazeHalf[a]
	(quot a 2)
)

(defn mazeHalfable[a]
	(even? a)
)

(defn distance[a b]
	(Math/abs (- a b))
)

(defn mazePath[a b sum maxSum maxDist]
	(if (= a b)
		sum
		(if (or (> sum maxSum) (> (distance a b) maxDist))
			maxSum
			(let [
				sumDouble (mazePath (mazeDouble a) b (inc sum) maxSum maxDist)
				sumAdd2 (mazePath (mazeAdd2 a) b (inc sum)  (min maxSum sumDouble) maxDist)
				sumHalf (if (mazeHalfable a) 
					 (mazePath (mazeHalf a) b (inc sum)  (min maxSum sumAdd2) maxDist)
					 maxSum
					)
				]
				(min sumHalf sumDouble sumAdd2)
			)
		)
	)
)

(defn numberMaze[a b]
	(mazePath a b 1 40 (* (distance a b) 10))
)

(defn numberMazeX[a b]
	(letfn[
		(mazeDouble[a]
			(+ a a)
		)

		(mazeAdd2[a]
			(+ a 2)
		)
		(mazeHalf[a]
			(quot a 2)
		)

		(mazeHalfable[a]
			(even? a)
		)

		(distance[a b]
			(Math/abs (- a b))
		)

		(mazePath[a b sum maxSum maxDist]
			(if (= a b)
				sum
				(if (or (> sum maxSum) (> (distance a b) maxDist))
					maxSum
					(let [
						sumDouble (mazePath (mazeDouble a) b (inc sum) maxSum maxDist)
						sumAdd2 (mazePath (mazeAdd2 a) b (inc sum)  (min maxSum sumDouble) maxDist)
						sumHalf (if (mazeHalfable a) 
							 (mazePath (mazeHalf a) b (inc sum)  (min maxSum sumAdd2) maxDist)
							 maxSum
							)
						]
						(min sumHalf sumDouble sumAdd2)
					)
				)
			)
		)

	]
	(mazePath a b 1 40 (* (distance a b) 10))
	)
)



(defn row[m n] (get m n))
(defn column [m n] (mapv #(get % n) m))
(defn diagonalLeft [m] (mapv (fn[row i] (get row i)) m '(0 1 2)))
(defn diagonalRight [m] (mapv (fn[row i] (get row i)) m '(2 1 0)))
(defn countSymbol [x sym] (count (filter #(= % sym) x)))
(defn winnable [x sym] 
	(and 
		(= (countSymbol x sym) 2)
		(= (countSymbol x :e) 1)
	)
)

(defn emptyPos[v] 
	(first 
		(filter (fn[n] (= (get v n) :e )) '(0 1 2) )
	)
)

(defn emptyPosInRow[m n]
	[n (emptyPos (row m n))]
)

(defn emptyPosInColumn[m n]
	[(emptyPos (column m n)) n]
)

(defn emptyPosInDiagonalLeft[m]
	(let [n (emptyPos (diagonalLeft m))]
		[n n]
	)
)

(defn emptyPosInDiagonalRight[m]
	(let [n (emptyPos (diagonalRight m))]
		[n (- 2 n)]
	)
)
(defn ticTacToeWin [sym m]
	(set (concat
		(map #(emptyPosInRow m %) (filter #(winnable (row m %) sym) '(0 1 2)))
		(map #(emptyPosInColumn m %) (filter #(winnable (column m %) sym) '(0 1 2)))
		(if (winnable (diagonalLeft m) sym) (list (emptyPosInDiagonalLeft m)) '())
		(if (winnable (diagonalRight m) sym) (list (emptyPosInDiagonalRight m)) '())
	))
)

(defn costAdd[cost a b]
	(if (= a b)
		cost
		(inc cost)
	)
)





(defn levRec[a b]
	(cond 
		(empty? a) (count b)
		(empty? b) (count a)
		:else (min
				(inc (levRec (rest a) b ))
				(inc (levRec  a (rest b)))
				(costAdd (levRec  (rest a) (rest b)) (first a) (first b))
			)		
	)
)

(defn levRec[a b mem]
	(let [existing (get mem [a b])]
		(if (some? existing)
			{ :val existing :mem mem} 
			(let [res 
				(cond 
					(empty? a) {:val (count b) :mem mem}
					(empty? b) {:val (count a) :mem mem}
					:else (let [ 
							res1 (levRec (rest a) b mem)
							res2 (levRec  a (rest b) (get res1 :mem))
							res3 (levRec  (rest a) (rest b) (get res2 :mem))
						]
						{
							:val (min 
								(inc (get res1 :val))
								(inc (get res2 :val))
								(costAdd (get res3 :val) (first a) (first b))
							)
							:mem (get res3 :mem)
						}
					)
				)
				]
				{ :val (get res :val) :mem (assoc (get res :mem) [a b] (get res :val))}
			)
		)
	)
)


(defn levehnstein[a b]
	(get (levRec (reverse a) (reverse b) {}):val)
)	


(defn cell [t y x]
	(get (get t y) x)
)

(defn alive [t y x]
	(= (cell t y x) \#) 
)

(defn alive01[t y x]
	(if (alive t y x)
		1
		0
	)
)

(defn aliveNeighbours[t y x]
	(+ 
		(alive01 t (dec y) (dec x))
		(alive01 t (dec y) x)
		(alive01 t (dec y) (inc x))
		(alive01 t y (dec x))
		(alive01 t y (inc x))
		(alive01 t (inc y) (dec x))
		(alive01 t (inc y) x)
		(alive01 t (inc y) (inc x))
	)
)

(defn toCell[t y x]
	(let 
		[ 
			alivexy (alive t y x)
			countNeighbours (aliveNeighbours t y x)
		]					 
		(cond
			(and alivexy (< countNeighbours 2)) \space
			(and alivexy (or (= countNeighbours 2)(= countNeighbours 3))) \#
			(and alivexy (> countNeighbours 3)) \space
			(and (not alivexy) (= countNeighbours 3)) \#
			:else (cell t y x)
		)
	)
)

(defn toRow[t y]
	(let [ columns (count (get t y))]
		(apply str 
			(map (partial toCell t y) (range columns))
		)
	)
)


(defn life[t]
	(let [ rows (count t) ]
		(map (partial toRow t) (range rows))
	) 
)



(defn cell [t coor]
	(let [
		y (:y coor)
		x (:x coor)
		]
		(get (get t y) x)
	)
)



(defn isCell [t c coor]
	 (= (cell t coor) c)
)

(defn isWall [t coor]
	 (isCell t \# coor)
)


(defn makeCoor[y x]
	{ :y y :x x}
)

(defn findC[t rows columns c]
	(let [ 
		allCells (for [x (range columns) y (range rows)] (makeCoor y x))
		]
		(first (filter #(isCell t c %) allCells))
	)
)

(defn findMouse[t rows columns]
	(findC t rows columns \M)
)

(defn findCheese[t rows columns]
	(findC t rows columns \C)
)

(defn coorValid[rows columns coor]
	(let [
	y (:y coor)
	x (:x coor)
	]
		(and (>= y 0) (>= x 0) (< y rows)(< x columns))
	)
)

(defn goRight[coor]
	(assoc coor :x (inc (:x coor)))
)

(defn goLeft[coor]
	(assoc coor :x (dec (:x coor)))
)

(defn goUp[coor]
	(assoc coor :y (dec (:y coor)))
)

(defn goDown[coor]
	(assoc coor :y (inc (:y coor)))
)


(defn mazePathRec[t rows columns coorC coorM visited]
	(cond 
		(contains? visited coorM) {:res false :vis visited}
		(isWall t coorM) {:res false :vis visited}
		(not (coorValid rows columns coorM)) {:res false :vis visited}
		(= coorM coorC) {:res true :vis visited}
		:else (let [
				newVisited (conj visited coorM)
				goneRight (mazePathRec t rows columns coorC (goRight coorM) newVisited)
				goneLeft (mazePathRec t rows columns coorC (goLeft coorM) (:vis goneRight))
				goneUp (mazePathRec t rows columns coorC (goUp coorM)(:vis goneLeft))
				goneDown (mazePathRec t rows columns coorC (goDown coorM)(:vis goneUp))

			] 
				{
					:res (or (:res goneRight) (:res goneLeft) (:res goneUp) (:res goneDown))
					:vis (:vis goneDown) 
				}
			)
		
	)
)


(defn maze[t]
	(let [
		rows (count t) 
		columns (count (get t 0))
		]
		(:res (mazePathRec t rows columns (findCheese t rows columns) (findMouse t rows columns) #{}))
	)	
)



(defn makeDance[& args]
  (reify
    clojure.lang.ISeq
    (seq [this] (if (empty? args) nil (distinct args)))
    Object 
     (toString [this] (clojure.string/join ", " (sort args)))
 )
)



(defn cell [t coor]
	(let [
		y (:y coor)
		x (:x coor)
		]
		(get (get t y) x)
	)
)

(defn cell [t coor]
	(let [
		y (:y coor)
		x (:x coor)
		]
		(get (get t y) x)
	)
)


(defn getColumnWihIndex[t i]
	(clojure.string/join "" (map #(get % i) t))
)

(defn rowsAndColumns[t]
	(let [
		columns (count (get t 0))
		]
		(concat t (map (partial getColumnWihIndex t) (range columns)))
	)
)



(defn removeSpaces[s]
	(clojure.string/replace s #" " "")
)

(defn removeSpacesInTable[t]
	(mapv removeSpaces t)
)

(defn extractWords[s]
	(clojure.string/split s #"#")
)

(defn extractWordsInSequence[x]
	(mapcat extractWords x)
)

(defn matchWord [w s]
	(not (nil? (re-matches (re-pattern (clojure.string/replace s #"_" ".")) w)))
)

(defn anyMatchWord [w x]
	(not (nil? (some (partial matchWord w) x)))
)


(defn puzzle [w t]
	(anyMatchWord w (extractWordsInSequence (rowsAndColumns (removeSpacesInTable t))))
)

(defn toSuit[c]
	(case c
		\D :diamond 
		\H :heart 
		\S :spade 
		\C :club)
)

(defn toRank[c]
	(ffirst (filter #(= (second %) c)
		(map-indexed vector "23456789TJQKA")))
)


(defn toCard[s]
	{ :suit (toSuit (first s)) :rank (toRank (second s)) }
)

(defn toCards[x]
	(map toCard x)
)


(defn allSameSuit[x]
	(let [target (:suit (first x))]
		(every? #(= (:suit %) target) x)
	)	
)

(defn ranks[x] (map :rank x))

(defn inSequence[x]	
	(let [ ranks (ranks x)
		   firstRank (first ranks)
		   consecutiveUp (map #(mod % 13) (range firstRank (+ firstRank 5)))
		   consecutiveDown (map #(mod % 13) (range firstRank (- firstRank 5) -1))
		] 	
		(or (= ranks consecutiveUp) (= ranks consecutiveDown))
	)
)

(defn rankFrequencies[x] (vals (frequencies (ranks x))))
(defn isStraightFlush[x] (and (allSameSuit x) (inSequence x)))
(defn isFourOfAKind[x] (>= (apply max (rankFrequencies x)) 4))
(defn isFullHouse[x] 
	(let [ranks (set (rankFrequencies x))]
		(and (contains? ranks 3)(contains? ranks 2))
	 )
)
(defn isFlush[x] (allSameSuit x))
(defn isStraight[x] (inSequence x))
(defn isThreeOfAKind[x] (contains? (set (rankFrequencies x)) 3))
(defn isTwoPair[x] 
	(let [ranks (rankFrequencies x)]
		(= (get (frequencies ranks) 2) 2)
	 )
)
(defn isPair[x] (contains? (set (rankFrequencies x)) 2))


(defn cards[xx]
	(let [x (toCards xx)]
		(cond
			 (isStraightFlush x) :straight-flush
			 (isFourOfAKind x) :four-of-a-kind			
			 (isFullHouse x) :full-house
			 (isFlush x) :flush
			 (isStraight x) :straight
			 (isThreeOfAKind x) :three-of-a-kind
			 (isTwoPair x) :two-pair
			 (isPair x) :pair
			 :else :high-card
		)
	)
)

(defn square [n]
	(* n n)
)

(defn powerSeq [a b]
	(if (> a b)
		'()
		(cons a (powerSeq (square a) b))
	)
)

(defn chars [x]
	(clojure.string/join "" (map str x))
)

(defn nextPerfectSquare[n]
	(square (int (Math/ceil (Math/sqrt n))))
)

(defn integrateWithStars [x]
	(let [
		cnt (count x)
		starsNumber (- (nextPerfectSquare cnt) cnt)]
		(concat x (repeat starsNumber \*))
	)
)

(defn fromRightDown[x y] {:coor { :x (inc x) :y (inc y) } :direction :left-down })
(defn fromRightDownWithCheck[x y previousCoors] 
	(let [newCoor (fromRightDown x y)]				
		(if(contains? previousCoors (:coor newCoor))
			(fromRightUp x y)
			newCoor
		)
	)
)
(defn fromLeftDown[x y] {:coor { :x (dec x) :y (inc y)} :direction :left-up })
(defn fromLeftDownWithCheck[x y previousCoors] 
	(let [newCoor (fromLeftDown x y)]			
		(if(contains? previousCoors (:coor newCoor))
			(fromRightDown x y)
			newCoor
		)
	)
)
(defn fromRightUp[x y] {:coor { :x (inc x) :y (dec y) } :direction :right-down })
(defn fromRightUpWithCheck[x y previousCoors] 
	(let [newCoor (fromRightUp x y)]				
		(if(contains? previousCoors (:coor newCoor))
			(fromLeftUp x y)
			newCoor
		)
	)
)
(defn fromLeftUp[x y] {:coor { :x (dec x) :y (dec y) } :direction :right-up })
(defn fromLeftUpWithCheck[x y previousCoors] 
	(let [newCoor (fromLeftUp x y)]				
		(if(contains? previousCoors (:coor newCoor))
			(fromLeftDown x y)
			newCoor
		)
	)
)




(defn nextCoor[coorDir previousCoors]
	(let [
		x (:x (:coor coorDir))
		y (:y (:coor coorDir))
		direction (:direction coorDir)
		previousCoorsSet (set previousCoors)
		newCoor (case direction
				:right-down (fromRightDownWithCheck x y previousCoorsSet)
				:left-down (fromLeftDownWithCheck x y previousCoorsSet)
				:left-up (fromLeftUpWithCheck x y previousCoorsSet)
				:right-up (fromRightUpWithCheck x y previousCoorsSet)
			)
		 ]
		 newCoor
	)
)

(defn spiralRes[n coorDir res]
	(if (zero? n)
		res
		(let [	
				value (:coor coorDir)
				newCoor (nextCoor coorDir res)
			]		
			(cons value (spiralRes (dec n) newCoor (cons (:coor newCoor) res)) )
		)
	)
)

(defn centerCoor[minX minY coor]
	{ 
		:x (- (:x coor) minX)
		:y (- (:y coor) minY) 
	}
)

(defn centerCoors[coors]
	(let [
			xs (map :x coors)
			ys (map :y coors)
			minX (apply min xs)
			minY (apply min ys)
		]
		(map (partial centerCoor minX minY) coors)
	)
)

(defn centerCoorsInSpiral[spiral]
	(map (fn[a b]{:coor a :char b}) (centerCoors (map :coor spiral)) (map :char spiral))
)

(defn toGraphicRow[charsByCoors maxX y]
	(clojure.string/join "" (map #(or (get charsByCoors {:x % :y y}) \space) (range maxX)))
)


(defn toGraphic[spiral]
	(let [
			coors (map :coor spiral)
			chars (map :char spiral)
			charsByCoors (zipmap coors chars)
			xs (map :x coors)
			ys (map :y coors)
			maxX (apply max xs)
			maxY (apply max ys)
		]
		(map (partial toGraphicRow charsByCoors  (inc maxX)) (range (inc maxY)))
	)
)



(defn spiral[a b]
	(let [
		powers (powerSeq a b)
		chrs (chars powers)
		chrsWithStars (integrateWithStars chrs)
		coors (spiralRes (count chrsWithStars) {:coor {:x 0 :y 0} :direction :right-down} [{:x 0 :y 0}])
		merged (map (fn[a b] {:coor a :char b }) coors chrsWithStars)
		]
		(toGraphic (centerCoorsInSpiral merged))
	)
)


(defn transitiveRes[relMap nextKey firstKey] 
	(let [vl (get relMap nextKey)]
		(if (nil? vl)
			#{}
			(conj (transitiveRes relMap vl firstKey)[firstKey vl])
		)
	)
)

(defn transitive[rels]
	(let [relMap (zipmap (map first rels) (map last rels))]
		(apply clojure.set/union (map #(transitiveRes relMap % %) (keys relMap)))
	)
)


(defn connectedSimple [a b]
	(or 
		(= (first a)(first b))
		(= (first a)(last b))
		(= (last a)(first b))
		(= (last a)(last b))
	)
)


(defn connectedTransitive [a b c nodes]
	(and
		(connected2 a c nodes)
		(connected2 b c nodes)
	)
)

(defn ors[x]
	(if (empty? x)
		false
		(reduce (fn[a b] (or a b)) x)
	)
)


(defn ands[x]
	(if (empty? x)
		false
		(reduce (fn[a b] (and a b)) x)
	)
)

(defn connected2 [a b nodes]
	(or 
		(connectedSimple a b) 
		(ors (map (fn[c] (connectedTransitive a b c (disj nodes c))) nodes))
	)
)

(defn connected[nodes]
	(ands 
		(for [a nodes b nodes]
			(if (= a b)
				true
				(connected2 a b (clojure.set/difference nodes #{a b}))
			)
		)
	)
)



(defn ors[x]
	(if (empty? x)
		false
		(reduce (fn[a b] (or a b)) x)
	)
)

(defn removeI [v n]
	(vec (concat (subvec v 0 n) (subvec v (inc n))))
	)


(defn allConnectedI[nodes connection lst i]
	(let [
		nodeI (get nodes i)
		nodesWithoutI (removeI nodes i)
		]
		(or 
			(and 
				(= (first nodeI) connection) 
				(allConnectedRes nodesWithoutI (last nodeI) lst)
			)
			(and 
				(= (last nodeI) connection) 
				(allConnectedRes nodesWithoutI (first nodeI) lst)
			)
		)
	) 
)

(defn allConnectedRes[nodes connection lst]
	(if (empty? nodes)
		(= connection lst)
		(ors
			(map (partial allConnectedI nodes connection lst) (range (count nodes)))
		)
	)
)

(defn allConnected[nodes]
	(if (nil? (second nodes))
		true
		(let [fst (first nodes)]
			(allConnectedRes (subvec nodes 1) (first fst) (last fst))
		)
	)
)	


(defn gety[coor] (first coor))
(defn getx[coor] (last coor))
(defn makeCoor[y x]
	[y x]
)

(defn setx[coor x]
	(makeCoor (gety coor) x)
)

(defn sety[coor y]
	(makeCoor y (getx coor))
)


(defn cell [t coor]
	(let [
		y (gety coor)
		x (getx coor)
		]
		(get (get t y) x)
	)
)


(defn isCell [t c coor]
	 (= (cell t coor) c)
)

(defn isEmpty[t coor]
	(isCell t 'e coor)
)

(defn incx[coor] (inc (getx coor)))
(defn decx[coor] (dec (getx coor)))
(defn incy[coor] (inc (gety coor)))
(defn decy[coor] (dec (gety coor)))


(defn goDir[coor dir]
	(case dir
		:right 	(setx coor (incx coor))
		:left (setx coor (decx coor))
		:up (sety coor (decy coor))
		:down (sety coor (incy coor))
		:rightup (makeCoor (decy coor) (incx coor))
		:rightdown (makeCoor (incy coor) (incx coor))
		:leftup (makeCoor (decy coor) (decx coor))
		:leftdown (makeCoor (incy coor) (decx coor))
	)
)


(defn opposite[c]
	(cond 
		(= c 'b) 'w
		(= c 'w) 'b
		:else nil
	)
)


(defn getMoveDirRes[t c opp dir coor stack]
	(let [
		newCoor (goDir coor dir)
		valNewCoor (cell t newCoor)
		]
		(cond 
			(nil? valNewCoor) nil
			(= valNewCoor 'e) nil
			(= valNewCoor c) stack
			(= valNewCoor opp) (getMoveDirRes t c opp dir newCoor (conj stack newCoor))
		)
	) 
)


(defn getMoveDir[t c coor dir] 
	(let [
		opp (opposite c)
		newCoor (goDir coor dir)
		]
		(if (isCell t opp newCoor)
			(getMoveDirRes t c opp dir newCoor #{newCoor})
			nil
		)
	)
)

(defn notnil?[x] (not (nil? x)))

(defn getMoves[t c coor]
	(if(isEmpty t coor)
		(apply clojure.set/union (filter notnil? (map (partial getMoveDir t c coor) [:right :left :up :down :rightup :rightdown :leftup :leftdown])))
		#{}
	)
)


(defn coors[]
	(for [y (range 4) x (range 4)]
		(makeCoor y x)
	)
)

(defn reversi[t c]
	(let [
			cs (coors)
			all (zipmap cs (map #(getMoves t c %) cs))
		]
		(select-keys all (for [[k v] all :when (seq v)] k)) 			
	)
)

(defn withoutSubTree[tree subTree]
	(cons (first tree) (filter #(not= subTree %) (rest tree)))
)

(defn addSubTree[tree subTree]
	(if (nil? subTree)
		tree
		(concat tree (list subTree))
	)
)

(defn nilIfEmpty[x] 
	(if (empty? x)
		nil
		x
	)
)

(defn reparentingRes[newRoot tree treeFromDownUp]
	(cond 
		(empty? tree) nil
		(= newRoot (first tree)) (cons newRoot (addSubTree  (rest tree) treeFromDownUp))
		:else (let [res  (mapcat #(reparentingRes newRoot % (addSubTree (withoutSubTree tree %) treeFromDownUp)) (rest tree) )]
			(nilIfEmpty res)
		)
	)
)

(defn reparenting[newRoot tree]
	(reparentingRes newRoot tree nil)
)


(defn goodTransition[s transition]
	(= s (first transition))
)

(defn goodTransitions[m s]
	(lazy-seq (filter (partial goodTransition s) (:transitions m)))
)

(defn toStatus[transitionTo]
	(last transitionTo)
)

(defn addLetter[r transitionTo]
	(cons (first transitionTo) r )
)

(defn makeParams [status letters]
	{:status status :letters letters}
)

(defn makeParamsFromTransitions[r group]
	(let [
		tos (last group)
		]
		(map #(makeParams (toStatus %) (addLetter r %)) tos)
	)
)

(defn dfaRes[m s r l]
	(mapcat #(dfaAccept m (:status %)(:letters %) (dec l)) 
		(mapcat (partial makeParamsFromTransitions r) (goodTransitions m s))
	)
)

(defn dfaAccept[m s r l]
	(cond 
		(zero? l) '()
		(contains? (:accepts m) s) (cons (apply str (reverse r)) (dfaRes m s r l)  )
		:else (dfaRes m s r l)
	)
)

(defn dfa[m]
	(dfaAccept m (:start m) [] 16)
)

(defn power2[n]
	(int (Math/pow 2 n))
)

(defn power2BiggerThan[n]
	(first (drop-while #(<= (power2 %) n)  (range)))
)

(defn isSet[m y x]
	(bit-test (get m y) x)
)


(defn isSetOffset[m fy y fx x]
	(isSet m (+ fy y) (+ fx x))
)

(defn ceil2[n] (int (Math/ceil (/ n 2))))
(defn unceil2[n] (- (* n 2) 1) )


(defn cnt[m fy fx v]
	(let [t (map #(isSetOffset m fy (first %) fx (second %)) v)]
		(println "cnt m" m " fy" fy " fx" fx " v" v " t" t)
		(if (every? identity  t)
			(count t)
			0
		)
	)
)


(defn triangleRightTop[n]
	(for [y (range n) x (range y n)]
		[y x]
	)
)

(defn triangleRightBottom[n]
	(for [y (range n) x (range (- (dec n) y) n)]
		[y x]
	)
)


(defn triangleLeftTop[n]
	(for [y (range n) x (range (- n y))]
		[y x]
	)
)

(defn triangleLeftBottom[n]
	(for [y (range n) x (range (inc y))]
		[y x]
	)
)


(defn triangleTop[n]
	(for [y (range n) x (range y (- (unceil2 n) y))]
		[y x]
	)
)

(defn triangleBottom[n]
	(for [y (range n) x (range (- (dec n) y) (+ n y))]
		[y x]
	)
)


(defn triangleRight[n]
	(for [x (range n) y (range (- (dec n) x) (+ n x))]
		[y x]
	)
)

(defn triangleLeft[n]
	(for [x (range n) y (range x (- (unceil2 n) x))]
		[y x]
	)
)


(defn shareCornerTrianglesRes[m cy cx cc]
	(if (>= cc 2)
		(let 
			[mx (apply max
					(for [y (range (inc (- cy cc))) x (range (inc (- cx cc)))]
						(max
							(cnt m y x (triangleRightTop cc))
							(cnt m y x (triangleLeftTop cc))
							(cnt m y x (triangleRightBottom cc))
							(cnt m y x (triangleLeftBottom cc))
						)
					)
				)
			]
			(if (zero? mx) 
				(shareCornerTrianglesRes m cy cx (dec cc))
				mx
			)
		)
		0
	)
)



(defn shareHorizontalTrianglesRes[m cy cx height]
	(if(>= height 2)
		(let 
			[mx (apply max
					(for [y (range (- (inc cy) height)) x (range (- (inc cx) (unceil2 height)))]
						(max
							(cnt m y x (triangleTop height))
							(cnt m y x (triangleBottom height))
						)
					)
				)
			]
			(if (zero? mx) 
				(shareHorizontalTrianglesRes m cy cx (dec height))
				mx
			)
		)
		0
	)
)

(defn shareVerticalTrianglesRes[m cy cx width]
	(if(>= width 2)
		(let 
			[mx (apply max
					(for [y (range (- (inc cy) (unceil2 width))) x (range (inc cx))]
						(max
							(cnt m y x (triangleLeft width))
							(cnt m y x (triangleRight width))
						)
					)
				)
			]
			(if(zero? mx) 
				(shareVerticalTrianglesRes m cy cx (dec width))
				mx
			)
		)
		0
	)
)


(defn shareTriangles[m]
	(let [
		maxRow (apply max m)
		cy (count m)
		cx (power2BiggerThan maxRow)
		res (max
			(shareCornerTrianglesRes m cy cx (min cy cx))
			(shareHorizontalTrianglesRes m cy cx (min cy (ceil2 cx)))
			(shareVerticalTrianglesRes m cy cx (min (ceil2 cy) cx))
		)
		]
		(if (zero? res)
			nil
			res
		)
	)
)

(defn latinSquaresX[v]
	(letfn[
		(fillnil[n]
			(case n
				1 [nil]
				2 [nil nil]
				3 [nil nil nil]
				4 [nil nil nil nil]
				(take n (repeat nil))
			)
		)

		(buildRowWithOffset[row maxx crow offset]
			(let [
				offsetRight (- maxx (+ crow offset))
				] 
				(cond 
					(=  crow maxx) row
					(and (zero? offset) (= offsetRight 1)) (conj row nil)
					(zero? offset) (vec (concat row (fillnil offsetRight)))
					(zero? offsetRight) (vec (concat (fillnil offset) row))
					(= offsetRight 1) (vec (concat (fillnil offset) (conj row nil)))
					:else (vec (concat (fillnil offset) row (fillnil offsetRight)))
				)
			)
		)

		(buildRow[row maxx]
			(let [
				crow (count row)
				maxOffset (- maxx crow)
				]
				(map #(buildRowWithOffset row maxx crow %) (range (inc maxOffset)))
			)
		)

		(buildRows[v maxx]
			(mapv #(buildRow % maxx) v)
		)

		(buildVectorsWithOffsets[rows cy resRows]
			(let [resCount (count resRows)]
				(if (= resCount cy)
					[resRows]
					(let [
							rowList (nth rows resCount)
						]
						(mapcat #(buildVectorsWithOffsets rows cy (conj resRows %)) rowList)
					)
				)		
			)
		)

		(hasSize[x size]
			(= (count x) size)
		)

		(columnX[square size x]
			(for [y (range size)]
				(nth (nth square y) x)
			)
		)

		(columnsVaried[square size]
			(every? #(hasSize (set (columnX square size %)) size)  (range size))
		)

		(rowsVaried[square size]
			(every? #(hasSize (set %) size) square)
		)

		(checkItemsNumSquareRes[rows res size]
			(let [resSize (count res)]
				(cond 
					(> resSize size) false
					(empty? rows) (= size resSize)
					:else (checkItemsNumSquareRes (rest rows) (clojure.set/union res (set (first rows))) size)	
				)
			)
		)

		(isLatinSquare[square size]
			(and
				(checkItemsNumSquareRes square #{} size)
				(rowsVaried square size)
				(columnsVaried square size)
			)
		)

		(rowHasNil[row fx lx]
			(or 
				(nil? (nth row fx)) 
				(nil? (nth row lx))
			)
		)

		(squareHasNil[v fy fx size]
			(let [
				lx (+ fx (dec size))
				rngY (range fy (+ fy size))
				]
				(some #(rowHasNil (nth v %) fx lx) rngY)
			)
		)

		(maxLengthSeq[v x res maxres]
			(cond 
				(empty? v) (max res maxres)
				(nil? (nth (first v) x)) (maxLengthSeq(rest v) x 0 (max res maxres))
				:else (maxLengthSeq (rest v) x (inc res) maxres)
			) 
		)

		(canTry[v size x]
			(>= (maxLengthSeq v x 0 0) size)
		)

		(generateSquaresX[v cy size x]
			(if (and (canTry v size x) (canTry v size (+ x (dec size))))
				(let [slices (mapv #(subvec % x (+ x size)) v)]
					(for [y (range (- (inc cy) size)) :when (not (squareHasNil v y x size))]
						(subvec slices y (+ y size))
					)
				)
			)
		)

		(generateSquares[v cy cx size]
			(mapcat #(generateSquaresX v cy size %) (range (- (inc cx) size)))
		)

		(generateAllSquares[vos cy cx size]
			(let [possible (distinct (mapcat #(generateSquares % cy cx size) vos))]
				(filter #(isLatinSquare % size) possible)
			)
		)

		]
		(let [
			cy (count v)
			maxx (apply max (map count v))
			rows (buildRows v maxx)
			vos (buildVectorsWithOffsets rows cy [])
			latins  (mapcat #(generateAllSquares vos cy maxx %) (range 2 (inc maxx)))
			]
			(frequencies (map count latins))
		)
	)
)



(defn sequenceX[cx] 
	(case cx
		1 [#{'a} #{'A}]
		2 [#{'a 'b} #{'a 'B} #{'A 'B} #{'A 'b}]
	)
)

(defn sequenceY[cy] 
	(case cy
		1 [#{'c} #{'c}]
		2 [#{'c 'd} #{'c 'D} #{'C 'D} #{'C 'd}]
	)
)

(defn toKarnoughForCell[truth keyX keyY]
	(let [
		keyAll (clojure.set/union keyX keyY)
		value (contains? truth keyAll)
		]
		{:key  keyAll :val value}
	)
)

(defn toKarnoughForX[truth keyY seqX]
	(mapv #(toKarnoughForCell truth % keyY) seqX)
)

(defn toKarnoughWithSize[truth len cy cx]
	(let [
			seqY (sequenceY cy)
			seqX (sequenceX cx)
		]
		(mapv #(toKarnoughForX truth % seqX) seqY)
	)
)


(defn toKarnough[truth]
	(let [
		len (count (first truth))
		cy (quot len 2)
		cx (- len cy)
		karnough (toKarnoughWithSize truth len cy cx)
		]
		karnough			
	)
)


(defn getInYX[karnough y x] 
	(get (get karnough y) x)
)



(defn searchAdjacent[karnough]
	(let [
		cy (count karnough)
		cx (count (first karnough))
		]
		(filter some 
			(for [y range cy]
				(for [x range cx]
					(if (:val (getInYX karnough y x))
						(makeGroups y x cy cx)
						[]
					)
				)
			)	
		)
	)
)

(defn incSizeX[rect]
	{:x (:x rect) :y (:y rect) :sizey (:sizey rect) :sizex (inc (:sizex rect)) }
)

(defn extendY[rect]
	{:x (:x rect) :y (:y rect) :sizey (inc (:sizey rect)) :sizex (:sizex rect) }
)

(defn decSizeX[rect]
	{:x (:x rect) :y (:y rect) :sizey (:sizey rect) :sizex (dec (:sizex rect)) }
)

(defn incmod[n cn]
	(mod (inc n) cn)
)

(defn incX[rect cx]
	{:x (incmod (:x rect) cx) :y (:y rect) :sizey (:sizey rect) :sizex (:sizex rect) }
)


(defn makeGroupsXRes [karnough y x cy cx res]
	(let [vl (getInYX karnough y x)]
		(cond 
			(= (:sizex res) cx) res
			(>= x cx) (makeGroupsXRes karnough y 0 cy cx res) 
			(:val vl) (makeGroupsXRes karnough y (inc x) cy cx (incSizeX res))
			:else res
		)
	)
)


(defn makeGroupsX [karnough y x cy cx]
	(makeGroupsXRes karnough y x cy cx {:y y :x x :sizey 1 :sizex 0})
)

(defn sameX[a b]
	(= (:x a) (:x b))
)

(defn sameSizeX[a b]
	(= (:sizex a) (:sizex b))
)

(defn aOverB[a b cy]
	(= (incmod (:y a) cy) (:y b)) 
)

(defn aSameXAndOverB[a b cy]
	(and  (sameX a b) (sameSizeX a b) (aOverB a b cy)) 
)

(defn composeAB[a b]
	{ :x (:x a) :y (:y a) :sizey (+ (:sizey b) (:sizey a)) :sizex (:sizex a)}
)

(defn composeY[group groups cy]
	(let [toCompose (filter #(aSameXAndOverB group % cy) groups)]
		(map #(composeAB group %) toCompose) 
	)
)

(defn composeYAll [groups cy]
	(mapcat #(composeY % groups cy) groups)
)


(defn makeGroupsXAll [karnough cy cx]
	(filter #(> (:sizex %) 0)
		(for [y (range cy) x (range cx)]
		(makeGroupsX karnough y x cy cx)
	))
)


(defn breakGroupX[group cx]
	(println group cx)
	(if (= (:sizex group) 1)
	 	(list group)
		(cons group 
			(concat 
				(breakGroupX (incX (decSizeX group) cx) cx)
				(breakGroupX (decSizeX group) cx)
			)
		)
	)
)


(defn breakGroupsX[groupsX cx]
	(set (mapcat #(breakGroupX % cx) groupsX))
)

(defn isOneCell[group]
	(and (= 1 (:sizex group))(= 1 (:sizey group)))
)

(defn removeOneCells[groups]
	(filter #(not (isOneCell %)) groups)
)

(defn aIncludesB[a b]
	(and 
		
	)
)

(defn removeIncluded[])


(defn vitch[truth]
	(let [
		karnough (toKarnough truth)
		cy (count karnough)
		cx (count (first karnough))
		original (makeGroupsXAll karnough cy cx)
		broken (breakGroupsX original cx)
		composed (composeYAll broken cy)
		together (set (concat composed broken))
		cleaned (removeOneCells together)
		]
		(pprint karnough)
		(println "original" original)
		(println "brokenG" broken)
		(println "composed" composed)
		(println "together" together)
		(println "cleaned" cleaned)
		cleaned
	)
)




(defn isBothDashesOrNeither[ax bx]
	(let [
		axDash (= ax '-)
		bxDash (= bx '_)
		]
		(or 
			(and axDash bxDash )
			(and (not axDash) (bxDash))
			)
	)
)

(defn haveDashesSamePlace[a b]
	(filter #(isBothDashesOrNeither (first %)(second %)) (map vector a b))
)	

(defn haveOneDifference[a b]
	(= (count (filter #(apply not= %) (map vector a b))) 1)
)

(defn haveOneDifferenceConsideringDashes[a b]
	(and
		(haveDashesSamePlace a b)
		(haveOneDifference a b)
	)
)


(defn dashForDifference[ax bx]
	(if (= ax bx)
		ax
		'-
	)
)

(defn markDifference[a b]
	(map dashForDifference a b)
)

(defn makeImplicant[implicant values previousLevel]
	{ :implicant implicant :values (set values) :previous previousLevel }
)


(defn nextLevelRes[toCheck others]
	(if (empty? others)
		'()
		(let [
			different (filter #(haveOneDifferenceConsideringDashes (:implicant toCheck) (:implicant %)) others)
			implicants (map #(makeImplicant (markDifference (:implicant toCheck) (:implicant %)) (set (concat (:values toCheck)(:values %))) #{(:implicant toCheck)(:implicant %)}   ) different)
			]
			(concat implicants (nextLevelRes (first others) (rest others) ))
		)
	)
)

(defn nextLevel[implicants]
	(nextLevelRes (first implicants) (rest implicants))
)

(defn fromTruthToSimpleImplicants[truth]
	(map #(makeImplicant % [%] #{}) truth)
)

(defn sortfn[v]
	(case v
		A 0
		a 0
		B 1
		b 1
		C 2
		c 2
		D 3
		d 3
	)
)

(defn toVectorTruth[truth ]
	(map #(sort-by sortfn %) truth)
)

(defn noPreviousImplicant[levelX previous]
	(not-any? #(contains? (:previous %) previous) levelX)
)


(defn noPreviousImplicants[levelX levelX_1]
	(filter #(noPreviousImplicant levelX (:implicant %)) levelX_1)
)

(defn primeImplicants[level1 level2 level3]
	(concat 
		(noPreviousImplicants level2 level1)
		(noPreviousImplicants level3 level2)
		level3
	)
)

(defn oneWithoutPrevious[implicant]
	{:implicant (:implicant implicant) :values (:values implicant)}
)

(defn withoutPrevious[implicants]
	(map oneWithoutPrevious implicants)
)


(defn areIndepedent[a b]
	(let [empt (empty? (clojure.set/intersection (:values a) (:values b)))]
		empt
	)
)


(defn essential?[toCheck others]
	(every? #(areIndepedent toCheck %) others)
)

(defn extractEssentials[implicants]
	(if (empty? implicants)
		'()
		(let [
			one (first implicants)
			others (rest implicants)
			]
			(if (essential? one others)
				(cons one (extractEssentials others))
				(extractEssentials others)
			)
		)
	)
)


(defn withoutValues[primes]
	(map #(:implicant %) primes)
)

(defn handledBy[implicants]
	(mapcat #(:values %) implicants)
)

(defn includesValues[a b]
	(every? #(contains? a %) b)
)

(defn handling[implicants values]
	(filter #(includesValues (:values % ) values) implicants)
)

(defn valueWithoutDashes[value]
	(filter #(not= % '-) value)
)


(defn withoutDashes[values]
	(map valueWithoutDashes values)
)

(defn toSets[values]
	(set (map set values))
)

(defn vitch[truth]
	(let [
		vecTruth (toVectorTruth truth)
		level1 (fromTruthToSimpleImplicants vecTruth)
		level2 (set (nextLevel level1))
		level3 (set (nextLevel level2))
		primesWithPrevious (set (primeImplicants level1 level2 level3))
		primes (set (withoutPrevious primesWithPrevious))
		essentials (set (extractEssentials (into '() primes)))
		notEssentials (clojure.set/difference primes essentials)
		handledByEssentials (set (handledBy essentials))
		notHandledByEssentials (clojure.set/difference (set vecTruth) handledByEssentials)
		notEssentialsHandling (set (handling notEssentials notHandledByEssentials))
		solution (clojure.set/union essentials notEssentialsHandling)
		]
		(toSets (withoutDashes (withoutValues solution)))
	)
)

(fn quine[] (let [q (char 34) p (char 116) s "(fn quine [] (let [q (char 34) p (char 116) s " t "] (str s q s q p q t q t)))" ] (str s q s q p q t q t)))