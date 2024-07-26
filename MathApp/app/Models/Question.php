<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Question extends Model
{
    use HasFactory;

    protected $table = 'questions'; // Specifies the table associated with the model

    protected $primaryKey = 'questionNo'; // Specifies the primary key field

    public $incrementing = true; // Specifies if the ID is auto-incrementing (true by default for integer IDs)

    protected $fillable = [
        'question', 
        'questionBankID',
    ]; // Allows mass assignment of these attributes

    // Define the relationship with the Qtnbank model
    public function qtnbank()
    {
        return $this->belongsTo(Qtnbank::class, 'questionBankID', 'questionBankID');
    }
}

