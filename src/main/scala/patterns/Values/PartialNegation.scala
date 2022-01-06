package parsing.Values

class PartialNegation(
               negationType: String, // Ex: "Shield"
               negationTypeId: String,
               negatedAmount: Int,
               negatedThrough: String, // Ex: "Absorbed""
               negatedThroughId: String,

               ){

  def getValues(): (String,String,Int,String,String) = {
    (negationType,negationTypeId,negatedAmount,negatedThrough,negatedThroughId)
  }

}
