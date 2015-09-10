;;;;;;
;; Taxis
;;;;;;

(define (domain TAXI)
	(:requirements :strips)
	
	(:predicates (multi-ride ?x)
				(cab-passenger-1-full)
				(cab-passenger-2-full)
				(is-passenger-1 ?x)
				(is-passenger-2 ?x)
				(is-cab ?x)
				(on ?x ?y)
				(connected ?x ?y)				
				)
				
	(:action move-to-1
		:parameters (?taxi ?sq1 ?sq2)
		:precondition (and (connected ?sq1 ?sq2) (on ?taxi ?sq1) (is-cab ?taxi))
		:effect
			(and (on ?taxi ?sq2) (not (on ?taxi ?sq1)))
	)
	(:action move-to-2
		:parameters (?taxi ?sq1 ?sq2)
		:precondition (and (connected ?sq2 ?sq1) (on ?taxi ?sq1) (is-cab ?taxi))
		:effect
			(and (on ?taxi ?sq2) (not (on ?taxi ?sq1)))
	)

	(:action pick-up-empty
		:parameters (?taxi ?person ?location)
		:precondition (and (on ?person ?location)
							(is-cab ?taxi)
							(on ?taxi ?location) 
							(not (cab-passenger-1-full))
							(not (cab-passenger-2-full)))
		:effect
			(and (not (on ?person ?location))
				 (is-passenger-1 ?person)
				 (cab-passenger-1-full))
	)
	(:action pick-up-1
		:parameters (?taxi ?person ?location)
		:precondition (and (on ?person ?location)
							(on ?taxi ?location)
							(is-cab ?taxi)
							(multi-ride ?person) 
							(not (cab-passenger-1-full))
							(cab-passenger-2-full))
		:effect
			(and (not (on ?person ?location))
				 (is-passenger-1 ?person)
				 (cab-passenger-1-full))
	)
	(:action pick-up-2
		:parameters (?taxi ?person ?location)
		:precondition (and (on ?person ?location)
							(on ?taxi ?location)
							(is-cab ?taxi)
							(cab-passenger-1-full)
							(not (cab-passenger-2-full))
							(multi-ride ?x))
		:effect
			(and (not (on ?person ?location))
				 (is-passenger-2 ?person)
				 (cab-passenger-2-full))
	)
	(:action drop-off-1
		:parameters (?taxi ?person ?location)
		:precondition (and (on ?taxi ?location)
							(cab-passenger-1-full)
							(is-cab ?taxi)
							(is-passenger-1 ?person))
		:effect 
			(and (on ?person ?location) 
				 (not (cab-passenger-1-full)) 
				 (not (is-passenger-1 ?person)))
	)
	(:action drop-off-2
		:parameters (?taxi ?person ?location)
		:precondition (and (on ?taxi ?location)
							(is-cab ?taxi)
							(cab-passenger-2-full) 
							(is-passenger-2 ?person))
		:effect 
			(and (on ?person ?location) 
				 (not (cab-passenger-2-full)) 
				 (not (is-passenger-2 ?person)))
	)
)