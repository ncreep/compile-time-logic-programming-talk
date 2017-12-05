can_write(string).
can_write(int).
can_write(boolean).

can_write(list(A)) :-
  can_write(A).
  
can_write(option(A)) :-
  can_write(A).
  
can_write(tuple(A, B)) :-
  can_write(A),
  can_write(B).
  
can_write(either(A, B)) :-
  can_write(A),
  can_write(B).
