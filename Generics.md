class Z
class A extends Z
class B extends A
class C extends B
class D

List<B>
List<? extends B>
List<? super B>

PECS 
- Producer extends
- Consumer super

Case 1:
List<B> = [new B()] 
List<B> = [new C(), new D()]

Case 2:
List<? extends B> 

The key thing to realize here is that the expression "? extends B" gets evaluated to a 
specific type (kind of). All the information we have about it is that it is a child class of
class B. So that means the statement can be rewritten as List<C> or List<D>. Now we can treat
the elements of that list of the superclass B since they are children classes and polymorphism,
but that doesn't change the fact that the list itself is a list of a child class type. Say it's
List<C>, does it make sense to add a object of type D?

You can only read types from this list because the only thing we know is that all the elements
can be treated as type B, not that they are all necessarily of type B.

Case 3:
List<? super B>

This means that any child class of B can be the final type of this list. This does not mean 
that super classes of B can be stored in this list. The expression "? super B" can get 
evaluated to let's say A. Now does it make sense to store objects of type Z in a list with 
type A? Does it however make sense to store objects of type A, B, C, D in this list? Yes 
because polymorphism. This wildcard like the extends keyword gives you a pseudo upper bound 
of the type that this list can be. That is anything along the generational line along the
class inheritance (imagine a grandparent, parent, child as a generational line).

What this gives you is freedom to add any object so long as it can be treated as an object
that is a super class of B. However we have no idea what the specific super class is so we 
can not read from it.

**Key thing**
The generic wildcards represent what type the list can get "evaluated" down to, not what items
can be added.

