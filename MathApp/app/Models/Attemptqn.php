<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Attemptqn extends Model
{
    use HasFactory;

    // Table name if it differs from the model name
    protected $table = 'attemptqn';

    // Primary key columns
    protected $primaryKey = ['attemptID', 'questionNo'];

    // If the primary key is not auto-incrementing
    public $incrementing = false;

    // Data types for primary key
    protected $keyType = 'int';

    // Fields that can be mass-assigned
    protected $fillable = [
        'score',
        'givenAnswer'
    ];

    // Relationships
    public function attempt()
    {
        return $this->belongsTo(Attempt::class, 'attemptID');
    }

    public function question()
    {
        return $this->belongsTo(Question::class, 'questionNo');
    }
}
