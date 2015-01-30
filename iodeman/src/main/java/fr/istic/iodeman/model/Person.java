package fr.istic.iodeman.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;


@Entity
@Table
public class Person implements Comparable<Person> {
	@Id
	@GeneratedValue
	private Integer id;
	
	private String firstName;
	private String lastName;
	private Role role;
	private String uid;
	private String email;

	@Formula(value="firstName || ' ' || lastName")
	private String fullName;
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int compareTo(Person p) {
		return this.getUid().compareTo(p.getUid());
	}
	
	
}
