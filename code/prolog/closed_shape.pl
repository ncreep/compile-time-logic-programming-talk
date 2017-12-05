closed_shape([], _).

closed_shape([Out|T], AllPorts) :-
  Out = out(_, _, Type),
  In = in(_, _, Type),
  exactly_once(connect(Out, In)),
  member(In, AllPorts),
  closed_shape(T, AllPorts).

closed_shape([In|T], AllPorts) :-
  In = in(_, _, Type),
  Out = out(_, _, Type),
  exactly_once(connect(Out, In)),
  member(Out, AllPorts),
  closed_shape(T, AllPorts).

all_ports_for_stage(Stage, AllPorts) :-
  find_all(in(Stage, _, _), InPorts),
  find_all(out(Stage, _, _), OutPorts),
  append(InPorts, OutPorts, AllPorts).

all_ports([], []).
all_ports([H|T], Result) :- 
  all_ports_for_stage(H, HeadPorts),
  all_ports(T, TailPorts),
  append(HeadPorts, TailPorts, Result).

closed_shape(Stages) :- 
  all_ports(Stages, AllPorts),
  closed_shape(AllPorts, AllPorts).
  
find_all(Goal, List) :-
  findall(Goal, Goal, List).

exactly_once(Goal) :-
  find_all(Goal, List),
  List = [Goal].

in(sink, 1, string).
in(broadcast, 1, int).
in(merge, 1, string).
in(merge, 2, string).
in(flow1, 1, int).
in(flow2, 1, int).
in(flow3, 1, string).
in(flow4, 1, int).

out(source, 1, int).
out(broadcast, 1, int).
out(broadcast, 2, int).
out(merge, 1, string).
out(flow1, 1, int).
out(flow2, 1, string).
out(flow3, 1, string).
out(flow4, 1, string).

connect(out(source, 1, int), in(flow1, 1, int)).
connect(out(flow1, 1, int), in(broadcast, 1, int)).
connect(out(broadcast, 1, int), in(flow2, 1, int)).
connect(out(broadcast, 2, int), in(flow4, 1, int)).
connect(out(flow2, 1, string), in(merge, 1, string)).
connect(out(flow4, 1, string), in(merge, 2, string)).
connect(out(merge, 1, string), in(flow3, 1, string)).
connect(out(flow3, 1, string), in(sink, 1, string)).

% closed_shape([source, sink, merge, broadcast, flow1, flow2, flow3, flow4]).
