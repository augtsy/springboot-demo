package com.example.demo.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CustomerList extends LinkedList<String> {
 
	@Override
	public Iterator<String> iterator(){
 
		Iterator<String> it = new Iterator<String>(){
			private int currentIndex;
			@Override
			public boolean hasNext(){
				return currentIndex <= 10;
			}
 
			@Override
			public String next(){
				currentIndex++;
				return "A";
			}
 
			@Override
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}
 
	public static void main(String[] args) {
		List<String> list = new CustomerList();
		list.add("A");
		list.add("B");
		list.add("C");
		list.forEach(System.out::print);
		System.out.println(" ");
		list.stream().forEach(System.out::print);
	}
 
}