/* Generated by AN DISI Unibo */ 
package it.unibo.coapclient

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Coapclient ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("CoapClient Started")
						itunibo.coap.observer.resourceObserverCoapClient.create( "coap://localhost:5684/fridge"  )
					}
					 transition( edgeName="goto",targetState="sendMsg", cond=doswitch() )
				}	 
				state("sendMsg") { //this:State
					action { //it:State
						forward("takeFood", "takeFood(1)" ,"fridge" ) 
						delay(2000) 
					}
					 transition( edgeName="goto",targetState="sendMsg2", cond=doswitch() )
				}	 
				state("sendMsg2") { //this:State
					action { //it:State
						forward("takeFood", "takeFood(2)" ,"fridge" ) 
						delay(2000) 
					}
					 transition( edgeName="goto",targetState="sendMsg", cond=doswitch() )
				}	 
			}
		}
}