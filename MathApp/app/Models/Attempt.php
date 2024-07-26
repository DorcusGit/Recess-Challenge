<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Attempt extends Model
{
    use HasFactory;

    // Table name if it differs from the model name
    protected $table = 'attempts';

    // Primary key column if it's not 'id'
    protected $primaryKey = 'attemptID';

    // If the primary key is not auto-incrementing
    public $incrementing = true;

    // Data types for primary key if not integer
    protected $keyType = 'int';

    // Fields that can be mass-assigned
    protected $fillable = [
        'startTime',
        'endTime',
        'score',
        'percentageMark',
        'participantID',
        'challengeNo'
    ];

    // Relationships
    public function participant()
    {
        return $this->belongsTo(Participant::class, 'participantID');
    }

    public function challenge()
    {
        return $this->belongsTo(Challenge::class, 'challengeNo');
    }
}
