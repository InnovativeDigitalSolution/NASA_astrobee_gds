/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Support class to handle holding ordered lists of sequenceables
 * @author tecohen
 *
 */
public abstract class SequenceHolder extends TypedObject implements PropertyChangeListener {
	private Logger logger = Logger.getLogger(SequenceHolder.class);

	protected List<Sequenceable> m_sequence = new ArrayList<Sequenceable>();	//sequence of stations, segments and commands

	protected int m_startTime = 0;					// milliseconds from 0 of start time (from beginning of plan)

	protected SequenceHolder() {
		super();
	}
	
	/**
	 * Autogenerated IDs of sequenceables will start with this
	 * @return
	 */
	@JsonIgnore
	public String getIdPrefix() {
		if (getClass().getSimpleName() != null){
			return getClass().getSimpleName();
		} else {
			if (this instanceof Sequenceable) {
				SequenceHolder parent = ((Sequenceable)this).getParent();
				if (parent != null){
					return parent.getIdPrefix();
				}
			}
		}
		return "";
	}

	/**
	 * Clear all contents of the sequence.  Does not affect start time.
	 */
	public void clearSequence(){
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);
		
		m_sequence = new ArrayList<Sequenceable>();
		firePropertyChange("sequence", oldSequence, m_sequence);
	}

	/**
	 * Add the exact sequenceables in this list to this sequence.  Note this will not clone them.
	 * @param sequence
	 */
	public void addSequence(List<Sequenceable> sequence){
		// gotta do this one by one to make sure the time accounting is correct
		for (Sequenceable s: sequence){
			addSequenceable(s);
		}
	}

	/**
	 * Append this sequenceable to the end of the sequence.
	 * @param sequenceable
	 */
	public void addSequenceable(Sequenceable sequenceable) {
		int size = m_sequence.size();
		addSequenceable(size, sequenceable);
	}

	/**
	 * 
	 * Add the sequenceable at the given index
	 * @param index
	 * @param sequenceable
	 * @return
	 */
	public boolean insertSequenceable(Sequenceable toInsert, Sequenceable reference) {
		int insertionIndex = m_sequence.indexOf(reference);
		if (insertionIndex == -1){
			return false;
		}
		addSequenceable(insertionIndex, toInsert);
		return true;
	}
	
	/**
	 * 
	 * Add the sequenceable at the given index
	 * @param index
	 * @param sequenceable
	 * @return
	 */
	public boolean addSequenceable(int index, Sequenceable sequenceable) {
		if (index > m_sequence.size() + 1 || index < 0){
			return false;
		}
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);
		m_sequence.add(index, sequenceable);

		handleSequenceAddition(sequenceable);
		firePropertyChange("sequence", oldSequence, m_sequence);
		return true;
	}

	/**
	 * Replace the sequenceable at the given index with the new sequenceable passed in
	 * @param index
	 * @param sequenceable
	 * @return true if this works
	 */
	@JsonIgnore
	public boolean setSequenceable(int index, Sequenceable sequenceable) {
		if (index > m_sequence.size() + 1 || index < 0){
			return false;
		}
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);

		sequenceable.addPropertyChangeListener(this);
		sequenceable.setParent(this);
		m_sequence.set(index, sequenceable);

		handleSequenceAddition(sequenceable);
		firePropertyChange("sequence", oldSequence, m_sequence);

		return true;
	}

	/**
	 * @return the list of sequenceables
	 */
	public List<Sequenceable> getSequence() {
		return m_sequence;
	}
//	
//	/** 
//	 * @return sequenceables and their children, with stations left out
//	 */
//	@JsonIgnore
//	public List<Sequenceable> getFlattenedSequenceWithoutStations() {
//		List<Sequenceable> flatList = new ArrayList<Sequenceable>();
//		Iterator<Sequenceable> list = m_sequence.iterator();
//		while(list.hasNext()) {
//			Sequenceable s = list.next();
//			if(s instanceof SequenceHolder) {
//				if(((SequenceHolder)s).isEmpty()) {
//					flatList.add(s);
//				} else {
//					flatList.addAll(((SequenceHolder) s).getFlattenedSequence());
//				}
//			}
//		}
//		return flatList;
//	}
//	

	/** 
	 * @return sequenceables and their children, with stations left out
	 */
	@JsonIgnore
	public List<Sequenceable> getJustTheCommands() {
		List<Sequenceable> cmds = new ArrayList<Sequenceable>();
		Iterator<Sequenceable> list = m_sequence.iterator();
		while(list.hasNext()) {
			Sequenceable s = list.next();
			if(s instanceof SequenceHolder) {
				cmds.addAll(((SequenceHolder) s).getJustTheCommands());
			} else {
				cmds.add(s);
			}
		}
		return cmds;
	}
	
	/**
	 * @return sequenceables and their children as a flat list
	 */
	@JsonIgnore
	public List<Sequenceable> getFlattenedSequence() {
		List<Sequenceable> flatList = new ArrayList<Sequenceable>();
		Iterator<Sequenceable> list = m_sequence.iterator();
		while(list.hasNext()) {
			Sequenceable s = list.next();
			flatList.add(s);
			if(s instanceof SequenceHolder) {
				flatList.addAll(((SequenceHolder) s).getFlattenedSequence());
			}
		}
		return flatList;
	}

	/**
	 * Sets the given sequence to be the sequence.
	 * @param sequence
	 */
	public void setSequence(List<Sequenceable> sequence){
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);
		m_sequence = new ArrayList<Sequenceable>();
		m_sequence.addAll(sequence);
		for (Sequenceable t : sequence){
			handleSequenceAddition(t);
		}
		firePropertyChange("sequence", oldSequence, m_sequence);
	}

	/**
	 * returns the sequenceable at the given index
	 * throws index out of bounds exception 
	 * @param index
	 * @return
	 */
	@JsonIgnore
	public Sequenceable getSequenceable(int index) {
		return m_sequence.get(index);
	}
	
	/**
	 * returns the first sequenceable if there is one, null otherwise.
	 * @param index
	 * @return
	 */
	@JsonIgnore
	public Sequenceable getFirstSequenceable() {
		try {
			if (!m_sequence.isEmpty()) {
				return m_sequence.get(0);
			}
		} catch (Exception ex){
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Remove the sequenceable at the given index
	 * @param index
	 * @return
	 */
	public Sequenceable removeSequenceable(int index) {
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);

		Sequenceable next = null;
		Sequenceable previous = null;
		Sequenceable cut = m_sequence.get(index);
		Sequenceable returnCopy;
		try {
			returnCopy = cut.clone();
		} catch (CloneNotSupportedException e) {
			returnCopy = null;
		}
		int changedIndex = index;
		if (cut instanceof Station) {
			// first, remove all the children of the Station that will be cut
			// to ensure no unwanted side effects
			Station cutStation = (Station) cut;
			for(int i=0; i<cutStation.getSequence().size(); i++) {
				cutStation.removeSequenceable(i);
			}
			
			// we have to remove either the previous or the next segment
			boolean segmentRemoved = false;

			Segment previousSegment = getPreviousSegment(index);
			Segment nextSegment = getNextSegment(index);

			// try removing the previous segment
			if (previousSegment != null ){

				Station previousStation = getPreviousStation(index);
				if (nextSegment != null){ 
					nextSegment.setPrevious(previousStation);
					previousStation.setNext(nextSegment);
				}
				changedIndex = indexOf(previousSegment);
				m_sequence.remove(previousSegment);
				segmentRemoved = true;
			}

			// try removing the next segment
			if (!segmentRemoved && nextSegment != null){

				Station nextStation = getNextStation(indexOf(nextSegment));
				if (previousSegment != null){
					previousSegment.setNext(nextStation);
					nextStation.setPrevious(previousSegment);
					changedIndex = indexOf(previousSegment);
				}
				m_sequence.remove(nextSegment);
				segmentRemoved = true;
			}
			
			if (segmentRemoved){
				index = m_sequence.indexOf(cut);
			}
		}
		
		// update the other sequenceables per this removal
		next = Plan.getNext(cut);
		previous = Plan.getPrevious(cut);
		
		boolean worked =  m_sequence.remove(cut);
		if (worked){
			cut.removePropertyChangeListener(this);
			cut.setParent(null);

			if (previous != null) {
				previous.setNext(next);
			}
			if (next != null){
				next.setPrevious(previous);
			}
			
			updateTimes(changedIndex);
			updateNames(changedIndex);

			firePropertyChange("sequence", oldSequence, m_sequence);
		}

		return returnCopy;
	}

	/**
	 * Remove the given sequenceable
	 * @param sequenceable
	 * @return
	 */
	public Sequenceable removeSequenceable(Sequenceable sequenceable) {
		int index = m_sequence.indexOf(sequenceable);
		if(index > -1) {
			return removeSequenceable(index);
		} else {
			return null;
		}
	}

	/**
	 * @param sequenceable
	 * @return true if this sequenceable is within the sequence
	 */
	public boolean containsSequenceable(Sequenceable sequenceable) {
		return m_sequence.contains(sequenceable);
	}

	/**
	 * Returns the index of the given sequenceable
	 * @param sequenceable
	 * @return
	 */
	public int indexOf(Sequenceable sequenceable) {
		return m_sequence.indexOf(sequenceable);
	}

	/**
	 * @return the size of the sequence.  
	 */
	public int size() {
		return m_sequence.size();
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource(); // so this is the command that fired the event change.
		
		if (source instanceof Sequenceable){
			int startInd = indexOf((Sequenceable)source);
			if (startInd >= 0){
				updateTimes(startInd);
			}
		}
		
		//In the case that the duration of a child element has changed, the event should be bubbled to listeners of this object
		//This is necessary so that start times etc. can be recalculated.
		
		if (propertyChangeEvent.getPropertyName().equals("duration") && (propertyChangeEvent.getOldValue() instanceof Double) && (propertyChangeEvent.getNewValue() instanceof Double)){
			double oldDuration = getCalculatedDuration();
			double newDuration = oldDuration;
			oldDuration = (Double)(propertyChangeEvent.getOldValue());
			newDuration = (Double)(propertyChangeEvent.getNewValue());
			if (oldDuration != newDuration) {
				firePropertyChange("calculatedDuration", oldDuration , newDuration); //CalculatedDuration is not a field, so this is not strictly correct,
			}
			//but listeners should be alerted to the fact that some aspect of this entity has changed, even if it's a calculated value.
		} else if ((source instanceof SequenceHolder) && (propertyChangeEvent.getPropertyName().equals("sequence"))){ // The sequence has changed at a lower level. This event must bubble updwards for listeners of this sequenceholder
			List<Sequenceable> oldSequence = (List<Sequenceable>)(propertyChangeEvent.getOldValue());
			List<Sequenceable> newSequence = (List<Sequenceable>)(propertyChangeEvent.getNewValue());			
			firePropertyChange("sequence", oldSequence , newSequence);
		} else {
			//TODO should this happen? TC
			//			logger.info("Want to send up " + propertyChangeEvent.getPropertyName());

			// something in the plan changed, and now the PlanTrace is listening for it.
			firePropertyChange("plan", propertyChangeEvent.getOldValue(), propertyChangeEvent.getNewValue());
		}
	}


	protected void handleSequenceAddition(Sequenceable added){
		added.setParent(this);
		int myindex = m_sequence.indexOf(added);
		
		if(myindex > -1) 
			added.autoName(myindex);
		
		Sequenceable previous = null;
		if (myindex > 0 ) {
			previous = m_sequence.get(myindex - 1);
		}
		
		Sequenceable next = null;
		if (myindex > -1 && myindex < m_sequence.size() - 1){
			next = m_sequence.get(myindex + 1);
		}

		if (previous != null){
			previous.setNext(added);
			added.setPrevious(previous);
		}
		
		if (next != null){
			added.setNext(next);
			next.setPrevious(added);
		}
		
		if(myindex > -1)
			updateTimes(myindex);
		//updateNames(myindex);


		//if this has a parent, then update the next in the parent's sequence per these changes.
		if (this instanceof Sequenceable){
			Sequenceable thisSequenceable = (Sequenceable)this;
			SequenceHolder parent = thisSequenceable.getParent();
			if (parent != null){
				parent.handleSequenceAddition(thisSequenceable);
			}
		}
		added.addPropertyChangeListener(this);
	}

	/**
	 * Update start times, ids and locations based on a change
	 * @param index start at this index
	 */
	public void updateTimes(int index){
		if (index >= 0 && !m_sequence.isEmpty()){
			// make sure everything knows what comes before it
			for(int i=index; i<getSequence().size(); i++){
				Sequenceable s = getSequenceable(i);

				// update the time
				if (i - 1 >= 0){
					Sequenceable previous = m_sequence.get(i - 1);
					s.setStartTime(previous.getStartTime() + previous.getCalculatedDuration());
				} else {
					s.setStartTime(getStartTime());
				}
				
				// update the children if any
				if (s instanceof SequenceHolder && !((SequenceHolder)s).isEmpty()){
					((SequenceHolder)s).updateTimes(0);
				}

			}
		}
	}
	
	/**
	 * Update ids based on a change
	 * @param index start at this index
	 */
	public void updateNames(int index){
		if (index >= 0 && !m_sequence.isEmpty()){
			// make sure everything knows what comes before it
			for(int i=index; i<getSequence().size(); i++){
				Sequenceable s = getSequenceable(i);
				// update the name
				s.autoName(i);
				
				// update the children if any
				if (s instanceof SequenceHolder && !((SequenceHolder)s).isEmpty()){
					((SequenceHolder)s).updateNames(0);
				}

			}
		}
	}

	/**
	 * @return calculated, estimated duration in ms
	 */
	@JsonIgnore
	public int getCalculatedDuration() {
		int result = 0;

		//TODO handle blocking vs nonblocking sequenceable
		for (Sequenceable s : getSequence()){
			result += s.getCalculatedDuration();
		}
		return result;
	}

	/**
	 * @return the startTime in ms
	 */
	@JsonIgnore
	public int getStartTime() {
		return m_startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	@JsonIgnore
	public void setStartTime(int startTime) {
		m_startTime = startTime;
		// update the start times in all the children!
		for (int i = 0; i < getSequence().size(); i++){
			Sequenceable s = getSequence().get(i);
			if (i == 0){
				s.setStartTime(startTime);
			} else {
				Sequenceable previous = getSequence().get(i -1);
				s.setStartTime(previous.getStartTime() + previous.getCalculatedDuration());
			}
		}
	}

	@Override
	public SequenceHolder clone() throws CloneNotSupportedException {
		SequenceHolder newSequenceHolder = (SequenceHolder) super.clone();

		List<Sequenceable> clones = new ArrayList<Sequenceable>();

		for (Sequenceable s : getSequence()){
			clones.add(s.clone());
		}

		newSequenceHolder.clearSequence();
		newSequenceHolder.addSequence(clones);

		return newSequenceHolder;
	}

	/**
	 * @return true if the sequence is empty
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return getSequence().isEmpty();
	}

	public Station getPreviousStation(int index) {
		ListIterator<Sequenceable> iterator = getSequence().listIterator(index); 
		while (iterator.hasPrevious()){ 
			Sequenceable s = iterator.previous();
			if (s instanceof Station){
				return (Station)s;
			}
		}
		return null;
	}

	public Station getNextStation(int index) {
		ListIterator<Sequenceable> iterator = getSequence().listIterator(index); 
		while (iterator.hasNext()){ 
			Sequenceable s = iterator.next();
			if (s instanceof Station){
				return (Station)s;
			}
		}
		return null;
	}

	@JsonIgnore
	public Segment getPreviousSegment(int index) {
		if (index >= 0){
			ListIterator<Sequenceable> iterator = getSequence().listIterator(index); 
			while (iterator.hasPrevious()){ 
				Sequenceable s = iterator.previous();
				if (s instanceof Segment){
					return (Segment)s;
				}
			}
		}
		return null;
	}

	@JsonIgnore
	public Segment getNextSegment(int index) {
		if (index >= 0){
		ListIterator<Sequenceable> iterator = getSequence().listIterator(index); 
			while (iterator.hasNext()){ 
				Sequenceable s = iterator.next();
				if (s instanceof Segment){
					return (Segment)s;
				}
			}
		}
		return null;
	}
	
	/**
	 * Climb up the tree and return the plan.
	 * @return
	 */
	@JsonIgnore
	public Plan getPlan(){
		if (this instanceof Plan){
			return (Plan)this;
		} else if (this instanceof Sequenceable){
			SequenceHolder parent = ((Sequenceable)this).getParent();
			if (parent != null){
				return parent.getPlan();
			}
		}
		return null;
	}
	
	public void autoNameChildren() {
		if (!isEmpty()) {
			int index = 0;
			for (Sequenceable s : getSequence()){
				s.autoName(index);
				index++;
			}
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());

		result = prime * result + ((m_sequence == null) ? 0 : m_sequence.hashCode());
		result = prime * result + m_startTime;

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if( !(o instanceof SequenceHolder)) {
			return false;
		}
		// don't check that the supers are equal, 
		// they must be the same object to be equal

		SequenceHolder other = (SequenceHolder)o;

		if(m_startTime != other.getStartTime()) {
			return false;
		}

		if(!m_sequence.equals(other.getSequence())) {
			return false;
		}

		return true;
	}

}
