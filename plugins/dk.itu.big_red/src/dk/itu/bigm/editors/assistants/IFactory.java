package dk.itu.bigm.editors.assistants;

public interface IFactory<T> {
	String getName();
	T newInstance();
}
