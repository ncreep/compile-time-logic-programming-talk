hobbit(frodo).
hobbit(bilbo).
hobbit(primula).
hobbit(mirabella).
hobbit(belladonna).
hobbit(hildbrand).
hobbit(sigismond).
hobbit(gerontius).

parent(belladonna, bilbo).
parent(primula, frodo).
parent(mirabella, primula).
parent(hildbrand, sigismond).
parent(gerontius, mirabella).
parent(gerontius, belladonna).
parent(gerontius, hildbrand).

grandparent(A, B) :-
  parent(A, C),
  parent(C, B).
  
siblings(A, B) :-
  parent(C, A),
  parent(C, B),
  A \= B.
  
first_cousin_once_removed(A, B) :-
  grandparent(C, B),
  siblings(C, D),
  parent(D, A).
