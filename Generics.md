class A
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

This means that any super class OR child class of B can exist in this list. It is not the
opposite of extends in that regard. Now the first part that any super class of B can exist
in this list is straightforward enough. That's literally what the code says. The key thing
here is that the generic wildcard describes what the type of the list is not what you can add.
So again think about the "? super B" being evaluated to a specific type let's say B. It's 
easy enough to see why you can add an object of class A. But because it's a list of type B,
you can also add objects of type C and D due to polymorphism. See Case 1. 

Again reading from this list is a trouble because what's the type of the variable that you are
going to store the result in? The only guarantee you have is that it will be of type Object
because it superclasses everything. You can put any super or child class because of a combination
of "evaluation" or polymorphism.


**Key thing**
The generic wildcards represent what type the list can get "evaluated" down to, not what items
can be added.

